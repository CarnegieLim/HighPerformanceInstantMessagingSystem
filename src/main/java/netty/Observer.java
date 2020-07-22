package netty;

/**
 * @author zhen
 */
public interface Observer {
    void update(boolean sucess, Object extraObj);
}
