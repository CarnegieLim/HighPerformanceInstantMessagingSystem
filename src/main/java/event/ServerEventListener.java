package event;

import protocal.Protocal;
import io.netty.channel.Channel;

/**
 * @author zhen
 */
public interface ServerEventListener {
    public int onVerifyUserCallBack(long userId, String token, String extra, Channel session);

    public void onUserLoginAction_CallBack(long userId, String extra, Channel session);

    public void onUserLogoutAction_CallBack(long userId, Object obj, Channel session);

    //	public boolean onTransBuffer_CallBack(String userId, String from_user_id
//			, String dataContent, String fingerPrint, int typeu, Channel session);
    public boolean onTransBuffer_C2S_CallBack(Protocal p, Channel session);

    //	public void onTransBuffer_C2C_CallBack(String userId, String from_user_id
//			, String dataContent, String fingerPrint, int typeu);
    public void onTransBuffer_C2C_CallBack(Protocal p);

    //	public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(String userId
//			, String from_user_id, String dataContent
//			, String fingerPrint, int typeu);
    public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(Protocal p);

}
