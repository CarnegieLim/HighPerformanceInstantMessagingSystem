package utils;

import bridge.QoS4ReciveDaemonC2B;
import netty.Observer;
import processor.BridgeProcessor;
import processor.OnlineProcessor;
import protocal.Protocal;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhen
 */
public class GlobalSendHelper {
    private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);

    public static void sendDataC2C(final BridgeProcessor bridgeProcessor
            , final Channel session, final Protocal pFromClient, final String remoteAddress
            , final ServerCoreHandler serverCoreHandler) throws Exception {
        // TODO just for DEBUG
        OnlineProcessor.getInstance().__printOnline();

        boolean needDelegateACK = false;
        if (Server.bridgeEnabled && !OnlineProcessor.isOnline(pFromClient.getTo())) {
            logger.debug("[IMCORE-netty<C2C>-桥接↑]>> 客户端" + pFromClient.getTo() + "不在线，数据[from:" + pFromClient.getFrom()
                    + ",fp:" + pFromClient.getFp() + "to:" + pFromClient.getTo() + ",content:" + pFromClient.getDataContent()
                    + "] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进"
                    + "行离线存储）【第一阶段APP+WEB跨机通信算法】！");

            if (pFromClient.isQoS()
                    && QoS4ReciveDaemonC2B.getInstance().hasRecieved(pFromClient.getFp())) {
                needDelegateACK = true;
            } else {
                boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
                if (toMQ) {
                    logger.debug("[IMCORE-netty<C2C>-桥接↑]>> 客户端" + remoteAddress + "的数据已跨机器送出成功【OK】。(数据[from:" + pFromClient.getFrom()
                            + ",fp:" + pFromClient.getFp() + ",to:" + pFromClient.getTo() + ",content:" + pFromClient.getDataContent()
                            + "]【第一阶段APP+WEB跨机通信算法】)");

                    if (pFromClient.isQoS()) {
                        needDelegateACK = true;
                    }
                } else {
                    logger.debug("[IMCORE-netty<C2C>-桥接↑]>> 客户端" + remoteAddress + "的数据已跨机器送出失败，将作离线处理了【NO】。(数据[from:" + pFromClient.getFrom()
                            + ",fp:" + pFromClient.getFp() + "to:" + pFromClient.getTo() + ",content:" + pFromClient.getDataContent()
                            + "]【第一阶段APP+WEB跨机通信算法】)");
//
//					boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
//							.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo()
//									, pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());

                    boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
                            .onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient);
                    if (pFromClient.isQoS() && offlineProcessedOK) {
                        needDelegateACK = true;
                    } else {
                        logger.warn("[IMCORE-netty<C2C>-桥接↑]>> 客户端" + remoteAddress + "的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
                                "功(或者完全没有)进行离线存储，此消息将被服务端丢弃【第一阶段APP+WEB跨机通信算法】！");
                    }
                }
            }

            if (needDelegateACK) {
                Observer resultObserver = new Observer() {
                    @Override
                    public void update(boolean receivedBackSendSucess, Object extraObj) {
                        if (receivedBackSendSucess) {
                            logger.debug("[IMCORE-netty<C2C>-桥接↑]【QoS_伪应答_C2S】向" + pFromClient.getFrom() + "发送" + pFromClient.getFp()
                                    + "的伪应答包成功,伪装from自：" + pFromClient.getTo() + "【第一阶段APP+WEB跨机通信算法】.");
                        }
                    }
                };

                LocalSendHelper.replyDelegateRecievedBack(session, pFromClient, resultObserver);
            }

            QoS4ReciveDaemonC2B.getInstance().addRecieved(pFromClient);
        } else {
            Observer resultObserver = new Observer() {
                @Override
                public void update(boolean sendOK, Object extraObj) {
                    if (sendOK) {
//						serverCoreHandler.getServerEventListener().onTransBuffer_C2C_CallBack(
//								pFromClient.getTo(), pFromClient.getFrom()
//								, pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
                        serverCoreHandler.getServerEventListener().onTransBuffer_C2C_CallBack(pFromClient);
                    } else {
                        logger.info("[IMCORE-netty<C2C>]>> 客户端" + remoteAddress + "的通用数据尝试实时发送没有成功，将交给应用层进行离线存储哦...");

//						boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
//								.onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient.getTo()
//										, pFromClient.getFrom(), pFromClient.getDataContent(), pFromClient.getFp(), pFromClient.getTypeu());
                        boolean offlineProcessedOK = serverCoreHandler.getServerEventListener()
                                .onTransBuffer_C2C_RealTimeSendFaild_CallBack(pFromClient);
                        if (pFromClient.isQoS() && offlineProcessedOK) {
                            try {
                                Observer retObserver = new Observer() {
                                    @Override
                                    public void update(boolean sucess, Object extraObj) {
                                        if (sucess) {
                                            logger.debug("[IMCORE-netty<C2C>]【QoS_伪应答_C2S】向" + pFromClient.getFrom() + "发送" + pFromClient.getFp()
                                                    + "的伪应答包成功,from=" + pFromClient.getTo() + ".");
                                        }
                                    }
                                };

                                LocalSendHelper.replyDelegateRecievedBack(session, pFromClient, retObserver);
                            } catch (Exception e) {
                                logger.warn(e.getMessage(), e);
                            }
                        } else {
                            logger.warn("[IMCORE-netty<C2C>]>> 客户端" + remoteAddress + "的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
                                    "功(或者完全没有)进行离线存储，此消息将被服务端丢弃！");
                        }
                    }
                }
            };

            LocalSendHelper.sendData(pFromClient, resultObserver);
        }
    }

    public static void sendDataS2C(BridgeProcessor bridgeProcessor, Protocal pFromClient
            , final Observer resultObserver) throws Exception {
        // TODO just for DEBUG
        OnlineProcessor.getInstance().__printOnline();

        boolean sucess = false;
        if (Server.bridgeEnabled && !OnlineProcessor.isOnline(pFromClient.getTo())) {
            logger.debug("[IMCORE-netty<S2C>-桥接↑]>> 客户端" + pFromClient.getTo() + "不在线，数据[from:" + pFromClient.getFrom()
                    + ",fp:" + pFromClient.getFp() + "to:" + pFromClient.getTo() + ",content:" + pFromClient.getDataContent()
                    + "] 将通过MQ直发Web服务端（彼时在线则通过web实时发送、否则通过Web端进"
                    + "行离线存储）【第一阶段APP+WEB跨机通信算法】！");

            boolean toMQ = bridgeProcessor.publish(pFromClient.toGsonString());
            if (toMQ) {
                logger.debug("[IMCORE-netty<S2C>-桥接↑]>> 服务端的数据已跨机器送出成功【OK】。(数据[from:" + pFromClient.getFrom()
                        + ",fp:" + pFromClient.getFp() + ",to:" + pFromClient.getTo() + ",content:" + pFromClient.getDataContent()
                        + "]【第一阶段APP+WEB跨机通信算法】)");
                sucess = true;
            } else {
                logger.error("[IMCORE-netty<S2C>-桥接↑]>> 服务端的数据已跨机器送出失败，请通知管理员检查MQ中间件是否正常工作【NO】。(数据[from:" + pFromClient.getFrom()
                        + ",fp:" + pFromClient.getFp() + "to:" + pFromClient.getTo() + ",content:" + pFromClient.getDataContent()
                        + "]【第一阶段APP+WEB跨机通信算法】)");
            }
        } else {
            LocalSendHelper.sendData(pFromClient, new Observer() {
                @Override
                public void update(boolean _sendSucess, Object extraObj) {
                    if (_sendSucess) {
                        _sendSucess = true;
                    } else {
                        logger.warn("[IMCORE-netty]>> 服务端的通用数据传输消息尝试实时发送没有成功，但上层应用层没有成" +
                                "功，请应用层自行决定此条消息的发送【NO】！");
                    }

                    if (resultObserver != null) {
                        resultObserver.update(_sendSucess, null);
                    }
                }
            });

            return;
        }

        if (resultObserver != null) {
            resultObserver.update(sucess, null);
        }
    }
}
