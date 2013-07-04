/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class MessagePrepare extends Message{
    
    private MessageRequest request;
    private int viewNumber;
    private int opeationNumber;
    private int lastCommited;
    
    public MessagePrepare(MessageRequest request, int viewNumber, int opeationNumber, int lastCommited ) {
        setMessageID(Constants.PREPARE);
        this.request = request;
        this.viewNumber = viewNumber;
        this.opeationNumber = opeationNumber;
        this.lastCommited = lastCommited;
    }
    
    @Override
    public String toString() {
        String s = "";
        s += "Message PREPARE" + Constants.NEWLINE;
        s += "ID: " + getMessageID() + Constants.NEWLINE;
        if(request.getOperation() != null) {
            s += "Operation: " + request.getOperation().getOperationID() + Constants.NEWLINE;
        }
        s += "Operation number: " + opeationNumber + Constants.NEWLINE;
        s += "View number: " + viewNumber + Constants.NEWLINE;
        s += "Last commited: " + lastCommited + Constants.NEWLINE;
        return s;
    }

    public int getLastCommited() {
        return lastCommited;
    }

    public void setLastCommited(int lastCommited) {
        this.lastCommited = lastCommited;
    }

    public MessageRequest getRequest() {
        return request;
    }

    public void setRequest(MessageRequest request) {
        this.request = request;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }

    public int getOpeationNumber() {
        return opeationNumber;
    }

    public void setOpeationNumber(int opeationNumber) {
        this.opeationNumber = opeationNumber;
    }
    
    
}
