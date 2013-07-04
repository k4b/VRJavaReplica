/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class MessagePrepareOK extends Message{
    
    private int viewNumber;
    private int operationNumber;
    private int replicaID;
    
    public MessagePrepareOK(int viewNumber, int operationNumber, int replicaID) {
        setMessageID(Constants.PREPAREOK);
        this.viewNumber = viewNumber;
        this.operationNumber = operationNumber;
        this.replicaID = replicaID;
    }
    
    @Override
    public String toString() {
        String s = "";
        s += "Message PREPAREOK" + Constants.NEWLINE;
        s += "ID: " + getMessageID() + Constants.NEWLINE;
        s += "Replica ID: " + replicaID + Constants.NEWLINE;
        s += "Operation number: " + operationNumber + Constants.NEWLINE;
        s += "View number: " + viewNumber + Constants.NEWLINE;
        return s;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public int getOperationNumber() {
        return operationNumber;
    }

    public int getReplicaID() {
        return replicaID;
    }
    
    
}
