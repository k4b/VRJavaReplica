/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.LinkedList;

/**
 *
 * @author karol
 */
public class ReplicaLog extends LinkedList<ReplicaLogEntry>{
    
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
        ReplicaLogEntry entry = null;
        for(int i = 0; i < this.size(); i++) {
            ReplicaLogEntry e = this.get(i);
            if(e.getOperationNumber() == operationNumber) {
                entry = e;
            }
        }
        return entry;
    }
}
