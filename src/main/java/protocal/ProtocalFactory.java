package protocal;

import com.google.gson.Gson;
import protocal.c.PKeepAlive;
import protocal.c.PLoginInfo;
import protocal.s.PErrorResponse;
import protocal.s.PKeepAliveResponse;
import protocal.s.PLoginInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhen
 */
public class ProtocalFactory {
    private static Logger logger = LoggerFactory.getLogger(ProtocalFactory.class);

    private static String create(Object c) {
        return new Gson().toJson(c);
    }

	public static <T> T parse(byte[] fullProtocalJASOnBytes, int len, Class<T> clazz)
	{
		return parse(CharsetHelper.getString(fullProtocalJASOnBytes, len), clazz);
	}

    public static <T> T parse(String dataContentOfProtocal, Class<T> clazz) {
        return new Gson().fromJson(dataContentOfProtocal, clazz);
    }

	public static Protocal parse(byte[] fullProtocalJASOnBytes, int len)
	{
		return parse(fullProtocalJASOnBytes, len, Protocal.class);
	}

    public static Protocal createPKeepAliveResponse(long to_user_id) {
        return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE
                , create(new PKeepAliveResponse()), 0, to_user_id);
    }

    public static PKeepAliveResponse parsePKeepAliveResponse(String dataContentOfProtocal) {
        return parse(dataContentOfProtocal, PKeepAliveResponse.class);
    }

    public static Protocal createPKeepAlive(long from_user_id) {
        return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE
                , create(new PKeepAlive()), from_user_id, 0);
    }

    public static PKeepAlive parsePKeepAlive(String dataContentOfProtocal) {
        return parse(dataContentOfProtocal, PKeepAlive.class);
    }

    public static Protocal createPErrorResponse(int errorCode, String errorMsg, long user_id) {
        return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR
                , create(new PErrorResponse(errorCode, errorMsg)), 0, user_id);
    }

    public static PErrorResponse parsePErrorResponse(String dataContentOfProtocal) {
        return parse(dataContentOfProtocal, PErrorResponse.class);
    }

    public static Protocal createPLoginoutInfo(long user_id) {
        return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT
                , null
                , user_id, 0);
    }

    public static Protocal createPLoginInfo(long userId, String token, String extra) {
        return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN
                , create(new PLoginInfo(userId, token, extra))
                , userId
                , 0);
    }

    public static PLoginInfo parsePLoginInfo(String dataContentOfProtocal) {
        return parse(dataContentOfProtocal, PLoginInfo.class);
    }

    public static Protocal createPLoginInfoResponse(int code
            , long user_id) {
        return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN
                , create(new PLoginInfoResponse(code))
                , 0
                , user_id
                , true, Protocal.genFingerPrint()
        );
    }

    public static PLoginInfoResponse parsePLoginInfoResponse(String dataContentOfProtocal) {
        return parse(dataContentOfProtocal, PLoginInfoResponse.class);
    }

    public static Protocal createCommonData(String dataContent, long from_user_id, long to_user_id
            , boolean QoS, String fingerPrint) {
        return createCommonData(dataContent, from_user_id, to_user_id, QoS, fingerPrint, -1);
    }

    public static Protocal createCommonData(String dataContent, long from_user_id, long to_user_id
            , boolean QoS, String fingerPrint, int typeu) {
        return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA
                , dataContent, from_user_id, to_user_id, QoS, fingerPrint, typeu);
    }

    public static Protocal createRecivedBack(long from_user_id, long to_user_id
            , String recievedMessageFingerPrint) {
        return createRecivedBack(from_user_id, to_user_id, recievedMessageFingerPrint, false);
    }

    public static Protocal createRecivedBack(long from_user_id, long to_user_id
            , String recievedMessageFingerPrint, boolean bridge) {
        Protocal p = new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED
                , recievedMessageFingerPrint, from_user_id, to_user_id);
        p.setBridge(bridge);
        return p;
    }
}
