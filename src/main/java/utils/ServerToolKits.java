package utils;

import processor.OnlineProcessor;
import protocal.CharsetHelper;
import protocal.Protocal;
import protocal.ProtocalFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @author zhen
 */
public class ServerToolKits {
    private static final Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);

    public static void setSenseMode(SenseMode mode) {
        int expire = 0;

        switch (mode) {
            case MODE_3S:
                // 误叛容忍度为丢3个包
                expire = 3 * 3 + 1;
                break;
            case MODE_10S:
                // 误叛容忍度为丢2个包
                expire = 10 * 2 + 1;
                break;
            case MODE_30S:
                // 误叛容忍度为丢2个包
                expire = 30 * 2 + 2;
                break;
            case MODE_60S:
                // 误叛容忍度为丢2个包
                expire = 60 * 2 + 2;
                break;
            case MODE_120S:
                // 误叛容忍度为丢2个包
                expire = 120 * 2 + 2;
                break;
            default:
                expire = -1;
                break;
        }

        if (expire > 0) {
            Server.SESION_RECYCLER_EXPIRE = expire;
        }
    }

    public static String clientInfoToString(Channel session) {
        SocketAddress remoteAddress = session.remoteAddress();
        String s1 = remoteAddress.toString();
        String sb = "{uid:" +
                OnlineProcessor.getUserIdFromSession(session) +
                "}" +
                s1;
        return sb;
    }

    public static String fromIOBuffer_JSON(ByteBuf buffer) throws Exception {
        byte[] req = new byte[buffer.readableBytes()];
        buffer.readBytes(req);
        return new String(req, CharsetHelper.DECODE_CHARSET);
    }

    public static Protocal fromIOBuffer(ByteBuf buffer) throws Exception {
        return ProtocalFactory.parse(fromIOBuffer_JSON(buffer), Protocal.class);
    }

    public enum SenseMode {
        /**
         * 对应于客户端的3秒心跳模式：此模式的用户非正常掉线超时时长为“3 * 3 + 1”秒。
         * <p>
         * 客户端心跳丢包容忍度为3个包。此模式为当前所有预设模式中体验最好，但
         * 客户端可能会大幅提升耗电量和心跳包的总流量。
         */
        MODE_3S,

        /**
         * 对应于客户端的10秒心跳模式：此模式的用户非正常掉线超时时长为“10 * 2 + 1”秒。
         * <p>
         * 客户端心跳丢包容忍度为2个包。
         */
        MODE_10S,

        /**
         * 对应于客户端的30秒心跳模式：此模式的用户非正常掉线超时时长为“30 * 2 + 2”秒。
         * <p>
         * 客户端心跳丢包容忍度为2个包。
         */
        MODE_30S,

        /**
         * 对应于客户端的60秒心跳模式：此模式的用户非正常掉线超时时长为“60 * 2 + 2”秒。
         * <p>
         * 客户端心跳丢包容忍度为2个包。
         */
        MODE_60S,

        /**
         * 对应于客户端的120秒心跳模式：此模式的用户非正常掉线超时时长为“120 * 2 + 2”秒。
         * <p>
         * 客户端心跳丢包容忍度为2个包。
         */
        MODE_120S
    }
}
