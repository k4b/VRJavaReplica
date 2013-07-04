/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class MessageReply extends Message{
    
    private static final String NEWLINE = System.getProperty("line.separator");
    
    private int viewNumber;
    private int requestNumber;
    private boolean result;
    
    public MessageReply(int viewNumber, int requestNumber, boolean result) {
        setMessageID(Constants.REPLY);
        this.viewNumber = viewNumber;
        this.requestNumber = requestNumber;
        this.result = result;
    }
    
    @Override
    public String toString() {
        String s = "";
        s += "Message REPLY" + NEWLINE;
        s += "ID: " + getMessageID() + NEWLINE;
        s += "View number: " + viewNumber + NEWLINE;
        s += "Request number: " + requestNumber + NEWLINE;
        s += "Result: " + result + NEWLINE;
        return s;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
