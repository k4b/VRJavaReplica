/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Karol
 */
public class TimeoutChecker extends Thread{
    
    int timeout;
    TimeoutTimer timer;
    int totalRunningTime;
    volatile boolean isRunning;
    private EventListenerList listenerList;
    
    public TimeoutChecker(int timeout) {
        this.timeout = timeout;
        timer = new TimeoutTimer(timeout);
        totalRunningTime = 0;
        isRunning = true;
        listenerList = new EventListenerList();
    }
    
    public void terminate() {
        isRunning = false;
        timer.stop();
    }
    
    void addTimeoutListener(TimeoutListener l) {
        listenerList.add(TimeoutListener.class, l);
      }

    void removeTimeoutListener(TimeoutListener l) {
      listenerList.remove(TimeoutListener.class, l);
    }
    
    protected void fireTimeout() {
        TimeoutListener[] ls = listenerList.getListeners(TimeoutListener.class);
        for (TimeoutListener l : ls) {
          l.timeout();
        }
      }
    
    public void restart() {
        timer.restart();
        isRunning = true;
    }

    @Override
    public void run() {
        while(isRunning) {
            int numSeconds = (int)((Math.random()*9)+1);
//            System.out.println("TimeoutChecker waiting " + numSeconds + " seconds.");
            try {
                Thread.sleep(numSeconds*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TimeoutChecker.class.getName()).log(Level.SEVERE, null, ex);
            }
            totalRunningTime += numSeconds;
            if(timer.isTimeOut()) {
//                System.out.println("Time is out! Waited " + totalRunningTime + " seconds.");
                fireTimeout();
                isRunning = false;
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TimeoutChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
