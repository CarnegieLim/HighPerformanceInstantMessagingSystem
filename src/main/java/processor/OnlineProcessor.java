package processor;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhen
 */
public class OnlineProcessor {
    public final static String USER_ID_IN_SESSION_ATTRIBUTE = "__user_id__";
    public static final AttributeKey<Long> USER_ID_IN_SESSION_ATTRIBUTE_ATTR =
            AttributeKey.newInstance(USER_ID_IN_SESSION_ATTRIBUTE);

    public static boolean DEBUG = false;
    private static Logger logger = LoggerFactory.getLogger(OnlineProcessor.class);
    private static OnlineProcessor instance = null;

    private ConcurrentMap<Long, Channel> onlineSessions = new ConcurrentHashMap<Long, Channel>();

    public static OnlineProcessor getInstance() {
        if (instance == null) {
            instance = new OnlineProcessor();
        }
        return instance;
    }

    private OnlineProcessor() {
    }

    public void putUser(long user_id, Channel session) {
        if (onlineSessions.containsKey(user_id)) {
            logger.debug("[IMCORE-netty]【注意】用户id=" + user_id + "已经在在线列表中了，session也是同一个吗？"
                    + (onlineSessions.get(user_id).hashCode() == session.hashCode()));
        }

        onlineSessions.put(user_id, session);

        __printOnline();// just for debug
    }

    public void __printOnline() {
        logger.debug("【@】当前在线用户共(" + onlineSessions.size() + ")人------------------->");
        if (DEBUG) {
            for (long key : onlineSessions.keySet()) {
                logger.debug("      > user_id=" + key + ",session=" + onlineSessions.get(key).remoteAddress());
            }
        }
    }

    public boolean removeUser(long user_id) {
        synchronized (onlineSessions) {
            if (!onlineSessions.containsKey(user_id)) {
                logger.warn("[IMCORE-netty]！用户id=" + user_id + "不存在在线列表中，本次removeUser没有继续.");
                __printOnline();// just for debug
                return false;
            } else {
                return (onlineSessions.remove(user_id) != null);
            }
        }
    }

    public Channel getOnlineSession(long user_id) {
        if (user_id == -1) {
            logger.warn("[IMCORE-netty][CAUTION] getOnlineSession时，作为key的user_id== -1.");
            return null;
        }

        return onlineSessions.get(user_id);
    }

    public ConcurrentMap<Long, Channel> getOnlineSessions() {
        return onlineSessions;
    }

    public static boolean isLogined(Channel session) {
        return session != null && getUserIdFromSession(session) != null;
    }

    public static Long getUserIdFromSession(Channel session) {
        Object attr;
        if (session != null) {
            attr = session.attr(USER_ID_IN_SESSION_ATTRIBUTE_ATTR).get();
            if (attr != null) {
                return (Long) attr;
            }
        }
        return null;
    }

    public static boolean isOnline(long userId) {
        return OnlineProcessor.getInstance().getOnlineSession(userId) != null;
    }
}
