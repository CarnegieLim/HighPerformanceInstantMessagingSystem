package qos;

/**
 * @author zhen
 */
public class QoS4SendDaemonS2C extends QoS4SendDaemonRoot {
    private static QoS4SendDaemonS2C instance = null;

    public static QoS4SendDaemonS2C getInstance() {
        if (instance == null) {
            instance = new QoS4SendDaemonS2C();
        }
        return instance;
    }

    private QoS4SendDaemonS2C() {
        super(0
                , 0
                , -1
                , true
                , "-本机QoS");
    }
}
