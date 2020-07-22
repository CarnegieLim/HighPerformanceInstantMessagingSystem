package protocal.c;

/**
 * @author zhen
 */
public class PLoginInfo {
    private long loginUserId = -1;
    private String loginToken = null;
    private String extra = null;

    public PLoginInfo(long loginUserId, String loginToken) {
        this(loginUserId, loginToken, null);
    }

    public PLoginInfo(long loginUserId, String loginToken, String extra) {
        this.loginUserId = loginUserId;
        this.loginToken = loginToken;
        this.extra = extra;
    }

    public long getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(long loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
