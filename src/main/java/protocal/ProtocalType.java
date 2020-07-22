package protocal;

/**
 * @author zhen
 */
public interface ProtocalType {
    /**
     * from client
     */
    interface C {
        int FROM_CLIENT_TYPE_OF_LOGIN = 0;
        int FROM_CLIENT_TYPE_OF_KEEP$ALIVE = 1;
        int FROM_CLIENT_TYPE_OF_COMMON$DATA = 2;
        int FROM_CLIENT_TYPE_OF_LOGOUT = 3;

        int FROM_CLIENT_TYPE_OF_RECIVED = 4;

        int FROM_CLIENT_TYPE_OF_ECHO = 5;
    }

    /**
     * from server
     */
    interface S {
        int FROM_SERVER_TYPE_OF_RESPONSE$LOGIN = 50;
        int FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE = 51;

        int FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR = 52;

        int FROM_SERVER_TYPE_OF_RESPONSE$ECHO = 53;
    }
}
