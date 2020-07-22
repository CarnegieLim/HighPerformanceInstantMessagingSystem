package processor;

import mq.MQProvider;
import netty.Observer;
import protocal.Protocal;
import protocal.ProtocalFactory;
import utils.LocalSendHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhen
 */
public abstract class BridgeProcessor extends MQProvider {
    private static Logger logger = LoggerFactory.getLogger(BridgeProcessor.class);

    public final static String IMMQ_DECODE_CHARSET = "UTF-8";

    public static String IMMQ_URI = "";
    public static String IMMQ_QUEUE_WEB2APP = "";
    public static String IMMQ_QUEUE_APP2WEB = "";

    public BridgeProcessor() {
        super(IMMQ_URI, IMMQ_QUEUE_APP2WEB, IMMQ_QUEUE_WEB2APP, "IMMQ", false);
    }

    @Override
    protected boolean work(byte[] contentBody) {
        try {
            String msg = new String(contentBody, IMMQ_DECODE_CHARSET);

            logger.info("[IMCORE-桥接↓] - [startWorker()中] 收到异构服务器的原始 msg：" + msg + ", 即时进行解析并桥接转发（给接收者）...");

            final Protocal p = ProtocalFactory.parse(msg, Protocal.class);
            p.setQoS(true);
            p.setBridge(true);

            Observer sendResultObserver = (sendOK, extraObj) -> {
                if (sendOK) {
                    realtimeC2CSuccessCallback(p);
                    logger.info("[IMCORE-桥接↓] - " + p.getFrom() + "发给" + p.getTo()
                            + "的指纹为" + p.getFp() + "的消息转发成功！【第一阶段APP+WEB跨机通信算法】");

                } else {
                    logger.info("[IMCORE-桥接↓]>> 客户端" + p.getFrom() + "发送给" + p.getTo() + "的桥接数据尝试实时发送没有成功("
                            + p.getTo() + "不在线)，将交给应用层进行离线存储哦... 【第一阶段APP+WEB跨机通信算法】");

                    boolean offlineProcessedOK = offlineC2CProcessCallback(p);
                    if (offlineProcessedOK) {
                        logger.debug("[IMCORE-桥接↓]>> 向" + p.getFrom() + "发送" + p.getFp()
                                + "的消息【离线处理】成功,from=" + p.getTo() + ". 【第一阶段APP+WEB跨机通信算法】");
                    } else {
                        logger.warn("[IMCORE-桥接↓]>> 客户端" + p.getFrom() + "发送给" + p.getTo() + "的桥接数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
                                "功(或者完全没有)进行离线存储，此消息将被服务端丢弃！ 【第一阶段APP+WEB跨机通信算法】");
                    }
                }
            };

            LocalSendHelper.sendData(p, sendResultObserver);

            return true;
        } catch (Exception e) {
            logger.warn("[IMCORE-桥接↓] - [startWorker()中] work()方法出错，本条错误消息被记录：" +
                    "" + e.getMessage(), e);
            return true;
        }
    }

    protected abstract void realtimeC2CSuccessCallback(Protocal p);

    protected abstract boolean offlineC2CProcessCallback(Protocal p);

}
