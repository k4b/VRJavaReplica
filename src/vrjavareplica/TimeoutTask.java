/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.concurrent.Callable;

/**
 *
 * @author karol
 */
class TimeoutTask implements Callable<String> {
    int timeout;
    
    public TimeoutTask(int timeout) {
     this.timeout = timeout;   
    }
    @Override
    public String call() throws Exception {
        Thread.sleep(timeout*1000);
        return "true";
    }
}
