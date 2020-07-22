package protocal.s;

/**
 * @author zhen
 */
public class PLoginInfoResponse {
    private int code = 0;

    public PLoginInfoResponse(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
