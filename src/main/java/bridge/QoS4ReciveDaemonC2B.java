package bridge;

import qos.QoS4ReciveDaemonRoot;

/**
 * @author zhen
 */
public class QoS4ReciveDaemonC2B extends QoS4ReciveDaemonRoot {
    private static QoS4ReciveDaemonC2B instance = null;

    public static QoS4ReciveDaemonC2B getInstance() {
        if (instance == null) {
            instance = new QoS4ReciveDaemonC2B();
        }
        return instance;
    }

    public QoS4ReciveDaemonC2B() {
        super(5 * 1000
                , 15 * 1000
                , true
                , "-桥接QoS！");
    }
}