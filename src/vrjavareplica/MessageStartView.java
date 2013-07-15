/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author Karol
 */
public class MessageStartView extends Message{
    
    private int viewNumber;
    private ReplicaLog log;
    private int lastCommited;
    
    public MessageStartView(int viewNumber, ReplicaLog log, int lastCommited) {
        setMessageID(Constants.STARTVIEW);
        this.viewNumber = viewNumber;
        this.log = log;
        this.lastCommited = lastCommited;
    }
    
    @Override
    public String toString() {
        String string = "";
        string += "Message STARTVIEW" + Constants.NEWLINE;
        string += "View number: " + viewNumber + Constants.NEWLINE;
        string += "Log size: " + log.size() + Constants.NEWLINE;
        string += "Last commited: " + lastCommited + Constants.NEWLINE;
        return string;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public ReplicaLog getLog() {
        return log;
    }

    public int getLastCommited() {
        return lastCommited;
    }
    
    
}
