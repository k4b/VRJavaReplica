/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Timeout;

import java.util.concurrent.Callable;

/**
 *
 * @author karol
 */
class MyTimeoutTask implements Callable<String> {
    int timeout;
    
    public MyTimeoutTask(int timeout) {
     this.timeout = timeout;   
    }
    @Override
    public String call() throws Exception {
        Thread.sleep(timeout*1000);
        return "true";
    }
}
