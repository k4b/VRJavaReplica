/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author karol
 */
public class VRCode implements Runnable{
    
    private int replicaID;
    private int port;
    private MessageProcessor messageProcessor;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Thread runningThread = null;
    
    public VRCode(int replicaID, int port, MessageProcessor messageProcessor) {
        this.replicaID = replicaID;
        this.port = port;
        this.messageProcessor = messageProcessor;
    }

    public void run(){
        LogWriter.log(replicaID, "Replica server started.") ;
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    LogWriter.log(replicaID, "Replica server stopped.") ;
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            new Thread(
                new ServerRunnable(replicaID, clientSocket, messageProcessor)
            ).start();
        }
        LogWriter.log(replicaID, "Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }
    
}
