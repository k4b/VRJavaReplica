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
    private int operationNumber;
    private int lastCommited;
    
    public MessagePrepare(MessageRequest request, int viewNumber, int operationNumber, int lastCommited ) {
        setMessageID(Constants.PREPARE);
        this.request = request;
        this.viewNumber = viewNumber;
        this.operationNumber = operationNumber;
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
        s += "Operation number: " + operationNumber + Constants.NEWLINE;
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

    public int getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(int operationNumber) {
        this.operationNumber = operationNumber;
    }
    
    
}
