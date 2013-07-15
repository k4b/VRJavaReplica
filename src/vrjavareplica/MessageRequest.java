/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class MessageRequest extends Message {
    
    private int clientID;
    private int requestNumber;
    private int viewNumber;
    private Operation operation;
    
    public MessageRequest(Operation operation, int clientID, int requestNumber, int viewNumber) {
        this.setMessageID(Constants.REQUEST);
        this.clientID = clientID;
        this.requestNumber = requestNumber;
        this.viewNumber = viewNumber;
        this.operation = operation;
    }
    
    @Override
    public String toString() {
        String s = "";
        s += "Message REQUEST" + Constants.NEWLINE;
        s += "ID: " + getMessageID() + Constants.NEWLINE;
        if(operation != null) {
            s += "Operation: " + operation.getOperationID() + Constants.NEWLINE;
        }
        s += "Request number: " + requestNumber + Constants.NEWLINE;
        s += "View number: " + viewNumber + Constants.NEWLINE;
        return s;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
    
    
}
