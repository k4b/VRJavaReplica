/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author karol
 */
public class ReplicaClientRunnable implements Runnable{

    protected Replica replica;
    protected String serverAddress;
    protected int serverPort;
    protected int messageID;
    protected Object message;
    protected boolean isStopped;
    protected Thread runningThread= null;
    protected Socket clientSocket;
    
    public ReplicaClientRunnable(Replica replica, String serverAddress, int serverPort, int messageID, Object message) {
        this.replica = replica;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.messageID = messageID;
        this.message = message;
    }
    
    @Override
    public void run() {
        LogWriter.log( replica.getReplicaID(), "Client started");
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            sendMessage();
        } catch (IOException e) {
            if(isStopped()) {
                LogWriter.log( replica.getReplicaID(), "Client communication crashed.") ;
                return;
            }
            throw new RuntimeException("Error accepting server connection", e);
        }
        LogWriter.log( replica.getReplicaID(), "Client communication Stopped.") ;
    }
    
    private synchronized boolean isStopped() {            
        return this.isStopped;
    }
    
    private void sendMessage() {
        switch(messageID) {
            case Constants.PREPARE :
                MessagePrepare prepare = (MessagePrepare) message;
                sendPrepare(prepare);
                break;
            
        }
    }
    
    private void sendPrepare(MessagePrepare prepare) {
        
        LogWriter.log(replica.getReplicaID(), "Sending message PREPARE" + Constants.NEWLINE + prepare.toString());
        DataOutputStream dataOutput = null;
                
        try {
            dataOutput = new DataOutputStream(clientSocket.getOutputStream());
            byte[] messageIDBytes = MyByteUtils.toByteArray(prepare.getMessageID());
            dataOutput.writeInt(messageIDBytes.length);
            dataOutput.write(messageIDBytes);
            //Whole client request message data
            byte[] operationIDBytes = MyByteUtils.toByteArray(prepare.getRequest().getOperation().getOperationID());
            dataOutput.writeInt(operationIDBytes.length);
            dataOutput.write(operationIDBytes);
            byte[] operationPathBytes = MyByteUtils.toByteArray(prepare.getRequest().getOperation().getPath());
            dataOutput.writeInt(operationPathBytes.length);
            dataOutput.write(operationPathBytes);
            if(prepare.getRequest().getOperation().getOperationID() == 1) {
                byte[] operationFile = prepare.getRequest().getOperation().getFile();
                if(operationFile != null) {
                    dataOutput.writeInt(operationFile.length);
                    dataOutput.write(operationFile);
                } else {
                    dataOutput.writeInt(1);
                    byte[] nullFile = new byte[1];
                    nullFile[0] = 0;
                    dataOutput.write(nullFile);
                }
            }
            byte[] clientIDBytes = MyByteUtils.toByteArray(prepare.getRequest().getClientID());
            dataOutput.writeInt(clientIDBytes.length);
            dataOutput.write(clientIDBytes);
            byte[] requestNumberBytes = MyByteUtils.toByteArray(prepare.getRequest().getRequestNumber());
            dataOutput.writeInt(requestNumberBytes.length);
            dataOutput.write(requestNumberBytes);
            byte[] viewNumberBytes = MyByteUtils.toByteArray(prepare.getRequest().getViewNumber());
            dataOutput.writeInt(viewNumberBytes.length);
            dataOutput.write(viewNumberBytes);
            byte[] replicaViewNumberBytes = MyByteUtils.toByteArray(replica.getViewNumber());
            dataOutput.writeInt(replicaViewNumberBytes.length);
            dataOutput.write(replicaViewNumberBytes);
            byte[] replicaOperationNumberBytes = MyByteUtils.toByteArray(replica.getOpNumber());
            dataOutput.writeInt(replicaOperationNumberBytes.length);
            dataOutput.write(replicaOperationNumberBytes);
            byte[] replicaLastCommitedBytes = MyByteUtils.toByteArray(replica.getLastCommited());
            dataOutput.writeInt(replicaLastCommitedBytes.length);
            dataOutput.write(replicaLastCommitedBytes);
            
            
        } catch (IOException ex) {
            Logger.getLogger(ReplicaClientRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dataOutput.flush();
                dataOutput.close();
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ReplicaClientRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
