package protocal;

import com.google.gson.Gson;

import java.util.UUID;

/**
 * @author zhen
 */
public class Protocal {
    private long msgId = -1;
    private boolean bridge = false;
    private int type = 0;
    private String dataContent = null;
    private long from = -1;
    private long to = -1;
    private String fp = null;
    private boolean QoS = false;
    private int typeu = -1;
    private transient int retryCount = 0;

    public Protocal(int type, String dataContent, long from, long to) {
        this(type, dataContent, from, to, -1);
    }

    public Protocal(int type, String dataContent, long from, long to, int typeu) {
        this(type, dataContent, from, to, false, null, typeu);
    }

    public Protocal(int type, String dataContent, long from, long to
            , boolean QoS, String fingerPrint) {
        this(type, dataContent, from, to, QoS, fingerPrint, -1);
    }

    public Protocal(int type, String dataContent, long from, long to
            , boolean QoS, String fingerPrint, int typeu) {
        this.type = type;
        this.dataContent = dataContent;
        this.from = from;
        this.to = to;
        this.QoS = QoS;
        this.typeu = typeu;

        if (QoS && fingerPrint == null) {
            fp = Protocal.genFingerPrint();
        } else {
            fp = fingerPrint;
        }
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDataContent() {
        return this.dataContent;
    }

    public void setDataContent(String dataContent) {
        this.dataContent = dataContent;
    }

    public long getFrom() {
        return this.from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return this.to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public String getFp() {
        return this.fp;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void increaseRetryCount() {
        this.retryCount += 1;
    }

    public boolean isQoS() {
        return QoS;
    }

    public void setQoS(boolean qoS) {
        this.QoS = qoS;
    }

    public boolean isBridge() {
        return bridge;
    }

    public void setBridge(boolean bridge) {
        this.bridge = bridge;
    }

    public int getTypeu() {
        return typeu;
    }

    public void setTypeu(int typeu) {
        this.typeu = typeu;
    }

    public String toGsonString() {
        return new Gson().toJson(this);
    }

    public byte[] toBytes() {
        return CharsetHelper.getBytes(toGsonString());
    }

    @Override
    public Object clone() {
        Protocal cloneP = new Protocal(this.getType()
                , this.getDataContent(), this.getFrom(), this.getTo(), this.isQoS(), this.getFp());
        cloneP.setBridge(this.bridge);
        cloneP.setTypeu(this.typeu);
        return cloneP;
    }

    public static String genFingerPrint() {
        return UUID.randomUUID().toString();
    }
}
