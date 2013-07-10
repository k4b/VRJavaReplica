/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author karol
 */
public class TimeoutTimer {
    
    private ExecutorService executor;
    private Future<String> future;
    private int timeout;
    
    public TimeoutTimer(int timeout) {
        this.timeout = timeout;
        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(new TimeoutTask(timeout));
    }
    
    public void restart() {
        executor.shutdownNow();
        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(new TimeoutTask(timeout));
    }
    
    public boolean isTimeOut() {
        boolean result = false;
        try {
            //timed out
            future.get(0, TimeUnit.SECONDS);
            result = true;
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(TimeoutTimer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            //Not timed out
        }
        return result;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
