/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.TimerTask;

/**
 *
 * @author Karol
 */
public class PrimaryTimeoutTask extends TimerTask {

    private Replica replica;
    
    public PrimaryTimeoutTask(Replica replica) {
        super();
        this.replica = replica;
    }
    
    @Override
    public void run() {
        replica.timeout();
    }
    
}
