import event.MessageQoSEventListenerS2C;
import event.ServerEventListener;
import idgen.snowflake.SnowflakeIDGenImpl;
import mq.MQProvider;
import processor.BridgeProcessor;
import processor.LogicProcessor;
import processor.OnlineProcessor;
import protocal.Protocal;
import protocal.ProtocalType;
import utils.LocalSendHelper;
import utils.ServerToolKits;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhen
 */
public class ServerCoreHandler extends MQProvider {
    private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);

    protected ServerEventListener serverEventListener = null;
    protected MessageQoSEventListenerS2C serverMessageQoSEventListener = null;

    protected LogicProcessor logicProcessor = null;

    protected BridgeProcessor bridgeProcessor = null;

    public final static String IMMQ_DECODE_CHARSET = "UTF-8";

    public static String IMMQ_URI = "amqp://admin:admin@ec2-54-234-130-205.compute-1.amazonaws.com";
    public static String IMMQ_QUEUE_MESSAGE = "q_message";

    public ServerCoreHandler() {
        super(IMMQ_URI, IMMQ_QUEUE_MESSAGE, IMMQ_QUEUE_MESSAGE, "IMMQ", false);
        super.start();

        logicProcessor = this.createLogicProcessor();

        if (Server.bridgeEnabled) {
            bridgeProcessor = this.createBridgeProcessor();
        }
    }

    protected LogicProcessor createLogicProcessor() {
        return new LogicProcessor(this);
    }

    protected BridgeProcessor createBridgeProcessor() {
        BridgeProcessor bp = new BridgeProcessor() {
            @Override
            protected void realtimeC2CSuccessCallback(Protocal p) {
//				serverEventListener.onTransBuffer_C2C_CallBack(
//						p.getTo(), p.getFrom(), p.getDataContent(), p.getFp(), p.getTypeu());
                serverEventListener.onTransBuffer_C2C_CallBack(p);
            }

            @Override
            protected boolean offlineC2CProcessCallback(Protocal p) {
//				return serverEventListener.onTransBuffer_C2C_RealTimeSendFaild_CallBack(
//						p.getTo(), p.getFrom(), p.getDataContent(), p.getFp(), p.getTypeu());
                return serverEventListener.onTransBuffer_C2C_RealTimeSendFaild_CallBack(p);
            }
        };
        return bp;
    }

    public void lazyStartupBridgeProcessor() {
        if (Server.bridgeEnabled && bridgeProcessor != null) {
            bridgeProcessor.start();
        }
    }

    public void exceptionCaught(Channel session, Throwable cause) throws Exception {
        logger.debug("[IMCORE-netty]此客户端的Channel抛出了exceptionCaught，原因是：" + cause.getMessage() + "，可以提前close掉了哦！", cause);
        session.close();
    }

    public void messageReceived(Channel session, ByteBuf bytebuf) throws Exception {
        Protocal pFromClient = ServerToolKits.fromIOBuffer(bytebuf);
        if (pFromClient.getMsgId() == -1) {
            pFromClient.setMsgId(SnowflakeIDGenImpl.getInstance().get(null).getId());
        }
        String remoteAddress = ServerToolKits.clientInfoToString(session);
//    	logger.info("---------------------------------------------------------");
        logger.info("[IMCORE-netty] << 收到客户端" + remoteAddress + "的消息:::" + pFromClient.toGsonString());
//        logger.info("[IMCORE] << type" + pFromClient.getType());

        switch (pFromClient.getType()) {
            case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED: {
                logger.info("[IMCORE-netty]<< 收到客户端" + remoteAddress + "的ACK应答包发送请求.");

                if (!OnlineProcessor.isLogined(session)) {
                    LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
                    return;
                }

                logicProcessor.processACK(pFromClient, remoteAddress);
                break;
            }
            case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA: {
                logger.info("[IMCORE-netty]<< 收到客户端" + remoteAddress + "的通用数据发送请求.");

                if (!OnlineProcessor.isLogined(session)) {
                    LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
                    return;
                }

                if (publish(pFromClient.toGsonString())) {
                    LocalSendHelper.replyDelegateRecievedBack(session, pFromClient, null);
                }

//                if(pFromClient.getTo() == 0) {
//                    logicProcessor.processC2SMessage(session, pFromClient, remoteAddress);
//                } else {
//                    logicProcessor.processC2CMessage(bridgeProcessor, session, pFromClient, remoteAddress);
//                }


//                if (serverEventListener != null) {
//                    if (!OnlineProcessor.isLogined(session)) {
//                        LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
//                        return;
//                    }
//
//                    if (pFromClient.getTo() == 0) {
//                        logicProcessor.processC2SMessage(session, pFromClient, remoteAddress);
//                    } else {
//                        logicProcessor.processC2CMessage(bridgeProcessor, session
//                                , pFromClient, remoteAddress);
//                    }
//                } else {
//                    logger.warn("[IMCORE-netty]<< 收到客户端" + remoteAddress + "的通用数据传输消息，但回调对象是null，回调无法继续.");
//                }
                break;
            }
            case ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE: {
                if (!OnlineProcessor.isLogined(session)) {
                    LocalSendHelper.replyDataForUnlogined(session, pFromClient, null);
                    return;
                } else {
                    logicProcessor.processKeepAlive(session, pFromClient, remoteAddress);
                }

                break;
            }
            case ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN: {
                logicProcessor.processLogin(session, pFromClient, remoteAddress);
                break;
            }
            case ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT: {
                logger.info("[IMCORE-netty]<< 收到客户端" + remoteAddress + "的退出登陆请求.");
                session.close();
                break;
            }
            case ProtocalType.C.FROM_CLIENT_TYPE_OF_ECHO: {
                pFromClient.setType(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$ECHO);
                LocalSendHelper.sendData(session, pFromClient, null);
                break;
            }
            default: {
                logger.warn("[IMCORE-netty]【注意】收到的客户端" + remoteAddress + "消息类型：" + pFromClient.getType() + "，但目前该类型服务端不支持解析和处理！");
                break;
            }
        }
    }

    public void sessionClosed(Channel session) throws Exception {
        long user_id = OnlineProcessor.getUserIdFromSession(session);
        if (user_id != -1) {
            Channel sessionInOnlinelist = OnlineProcessor.getInstance().getOnlineSession(user_id);

            logger.info("[IMCORE-netty]" + ServerToolKits.clientInfoToString(session) + "的会话已关闭(user_id=" + user_id + ")了...");

            if (sessionInOnlinelist != null && session != null && session == sessionInOnlinelist) {
                OnlineProcessor.getInstance().removeUser(user_id);

                if (serverEventListener != null) {
                    serverEventListener.onUserLogoutAction_CallBack(user_id, null, session);
                } else {
                    logger.debug("[IMCORE-netty]>> 会话" + ServerToolKits.clientInfoToString(session)
                            + "被系统close了，但回调对象是null，没有进行回调通知.");
                }
            } else {
                logger.warn("[IMCORE-netty]【2】【注意】会话" + ServerToolKits.clientInfoToString(session)
                        + "不在在线列表中，意味着它是被客户端弃用的，本次忽略这条关闭事件即可！");
            }
        } else {
            logger.warn("[IMCORE-netty]【注意】会话" + ServerToolKits.clientInfoToString(session)
                    + "被系统close了，但它里面没有存放user_id，它很可能是没有成功合法认证而被提前关闭，从而正常释放资源。");
        }
    }

    public void sessionCreated(Channel session) throws Exception {
        logger.info("[IMCORE-netty]与" + ServerToolKits.clientInfoToString(session) + "的会话建立(channelActive)了...");
    }

    public ServerEventListener getServerEventListener() {
        return serverEventListener;
    }

    void setServerEventListener(ServerEventListener serverEventListener) {
        this.serverEventListener = serverEventListener;
    }

    public MessageQoSEventListenerS2C getServerMessageQoSEventListener() {
        return serverMessageQoSEventListener;
    }

    void setServerMessageQoSEventListener(MessageQoSEventListenerS2C serverMessageQoSEventListener) {
        this.serverMessageQoSEventListener = serverMessageQoSEventListener;
    }

    public BridgeProcessor getBridgeProcessor() {
        return bridgeProcessor;
    }
}
