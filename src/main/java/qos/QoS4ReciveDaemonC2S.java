package qos;

/**
 * @author zhen
 */
public class QoS4ReciveDaemonC2S extends QoS4ReciveDaemonRoot
{
	private static QoS4ReciveDaemonC2S instance = null;
	
	public static QoS4ReciveDaemonC2S getInstance()
	{
		if(instance == null) {
			instance = new QoS4ReciveDaemonC2S();
		}
		return instance;
	}
	
	public QoS4ReciveDaemonC2S()
	{
		super(0 
			, 0
			, true
			, "-本机QoS");
	}
}
