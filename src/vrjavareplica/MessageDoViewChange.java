/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author Karol
 */
public class MessageDoViewChange extends Message {
    
    private int replicaID;
    private int viewNumber;
    private ReplicaLog log;
    private int lastCommited;
    
    public MessageDoViewChange(int replicaID, int viewNumber, ReplicaLog log, int latestCommitedOpNumber) {
        this.replicaID = replicaID;
        this.viewNumber = viewNumber;
        this.log = log;
        this.lastCommited = latestCommitedOpNumber;
        setMessageID(Constants.DOVIEWCHANGE);
    }

    @Override
    public String toString() {
        String string = "";
        string += "Message DOVIEWCHANGE" + Constants.NEWLINE;
        string += "ID: " + getMessageID() + Constants.NEWLINE;
        string += "Replica ID: " + replicaID + Constants.NEWLINE;
        string += "View number: " + viewNumber + Constants.NEWLINE;
        string += "Latest commited operation number: " + lastCommited + Constants.NEWLINE;
        string += "Log size: " + log.size() + Constants.NEWLINE;
//        if(log != null && log.size() > 0) {
//            for(int i = 0; i < log.size(); i++) {
//                string += "LogEntry: " + log.get(i).toString() + Constants.NEWLINE;
//            }
//        }
        return string;
    }

    public int getReplicaID() {
        return replicaID;
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
    
    public boolean isLogMoreRecent(MessageDoViewChange compared) {
        boolean result = false;
        if(compared.getLog() == null || compared.getLog().size() == 0) {
            return false;
        } else if (this.log.size() == 0) {
            return true;
        } else {
            ReplicaLogEntry thisLastEntry = this.log.getLast();
            ReplicaLogEntry comparedLastEntry = compared.getLog().getLast();
            if(thisLastEntry.getRequest().getViewNumber() > comparedLastEntry.getRequest().getViewNumber()) {
                result = true;
            } else if (thisLastEntry.getRequest().getViewNumber() 
                    == comparedLastEntry.getRequest().getViewNumber()
                    && thisLastEntry.getOperationNumber() > comparedLastEntry.getOperationNumber()) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }
}
