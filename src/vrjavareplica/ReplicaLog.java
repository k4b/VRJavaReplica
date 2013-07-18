/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author karol
 */
public class ReplicaLog extends LinkedList<ReplicaLogEntry>{
    
    public ReplicaLog() {
        super();
    }
    
    public ReplicaLog(Collection<ReplicaLogEntry> c) {
        super(c);
    }
    
    public int findRequestNumber(int operationNumber) {
        int value = 0;
        for(int i = 0; i < this.size(); i++) {
            ReplicaLogEntry entry = this.get(i);
            if(entry.getOperationNumber() == operationNumber) {
                value = entry.getRequest().getRequestNumber();
            }
        }
        return value;
    }
    
    public ReplicaLogEntry findEntry(int operationNumber) {
        for(int i = 0; i < this.size(); i++) {
            ReplicaLogEntry e = this.get(i);
            if(e.getOperationNumber() == operationNumber) {
                return e;
            }
        }
        return null;
    }
    
    public boolean isMoreRecent(ReplicaLog compared) {
        boolean result = false;
        if(compared == null || compared.size() == 0) {
            return false;
        } else if (this.size() == 0) {
            return true;
        } else {
            ReplicaLogEntry thisLastEntry = this.getLast();
            ReplicaLogEntry comparedLastEntry = compared.getLast();
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
    
    public ReplicaLog removeCommitedSubset(ReplicaLog subset) {
        ReplicaLog copy = new ReplicaLog(this);
        for(ReplicaLogEntry entry : subset) {
            if(entry.isExecuted() && copy.contains(entry)) {
                copy.remove(entry);
            }
        }
        return copy;
    }
    
    public boolean contains(ReplicaLogEntry entry) {
        ReplicaLogEntry e = findEntry(entry.getOperationNumber());
        return (e == null) ? false : true;
    }
    
    public boolean remove(ReplicaLogEntry entry) {
        ReplicaLogEntry e = findEntry(entry.getOperationNumber());
        boolean result = super.remove(e);
        return result;
    }
    
}
