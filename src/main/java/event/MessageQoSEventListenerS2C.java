package event;

import protocal.Protocal;

import java.util.ArrayList;

/**
 * @author zhen
 */
public interface MessageQoSEventListenerS2C {
    void messagesLost(ArrayList<Protocal> lostMessages);

    void messagesBeReceived(String theFingerPrint);
}
