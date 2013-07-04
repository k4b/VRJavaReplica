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
    
    protected int port;
    protected MessageProcessor messageProcessor;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    
    public VRCode(int port, MessageProcessor messageProcessor) {
        this.port = port;
        this.messageProcessor = messageProcessor;
    }

    public void run(){
        System.out.println("Replica server started.") ;
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
                    System.out.println("Replica server stopped.") ;
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            new Thread(
                new ServerRunnable(clientSocket, messageProcessor)
            ).start();
        }
        System.out.println("Server Stopped.") ;
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
