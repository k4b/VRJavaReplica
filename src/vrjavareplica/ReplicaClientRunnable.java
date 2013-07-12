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
            case Constants.PREPAREOK :
                MessagePrepareOK prepareOK = (MessagePrepareOK) message;
                sendPrepareOK(prepareOK);
                break;
            case Constants.DOVIEWCHANGE :
                MessageDoViewChange doViewChange = (MessageDoViewChange) message;
                sendDoViewChange(doViewChange);
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
    
    private void sendPrepareOK(MessagePrepareOK prepareOK) {
        LogWriter.log(replica.getReplicaID(), "Sending message PREPAREOK" + Constants.NEWLINE + prepareOK.toString());
        DataOutputStream dataOutput = null;
        try {
            dataOutput = new DataOutputStream(clientSocket.getOutputStream());
            byte[] messageIDBytes = MyByteUtils.toByteArray(prepareOK.getMessageID());
            dataOutput.writeInt(messageIDBytes.length);
            dataOutput.write(messageIDBytes);
            byte[] viewNumberBytes = MyByteUtils.toByteArray(prepareOK.getViewNumber());
            dataOutput.writeInt(viewNumberBytes.length);
            dataOutput.write(viewNumberBytes);
            byte[] operationNumberBytes = MyByteUtils.toByteArray(prepareOK.getOperationNumber());
            dataOutput.writeInt(operationNumberBytes.length);
            dataOutput.write(operationNumberBytes);
            byte[] replicaIDBytes = MyByteUtils.toByteArray(prepareOK.getReplicaID());
            dataOutput.writeInt(replicaIDBytes.length);
            dataOutput.write(replicaIDBytes);
            
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
    
    private void sendDoViewChange(MessageDoViewChange doViewChange) {
        LogWriter.log(replica.getReplicaID(), "Sending message DOVIEWCHANGE" + Constants.NEWLINE + doViewChange.toString());
        DataOutputStream dataOutput = null;
        try {
            dataOutput = new DataOutputStream(clientSocket.getOutputStream());
            byte[] messageIDBytes = MyByteUtils.toByteArray(doViewChange.getMessageID());
            dataOutput.writeInt(messageIDBytes.length);
            dataOutput.write(messageIDBytes);
            byte[] viewNumberBytes = MyByteUtils.toByteArray(doViewChange.getViewNumber());
            dataOutput.writeInt(viewNumberBytes.length);
            dataOutput.write(viewNumberBytes);
            //log
            ReplicaLog log = doViewChange.getLog();
            if(log != null && log.size() > 0) {
                dataOutput.writeInt(log.size());
                for(int i = 0; i < log.size(); i++) {
                    //BEGINING OF REQUEST --------------------------------------------------------------------
                    MessageRequest request = log.get(i).getRequest();
                    byte[] requestMessageIDBytes = MyByteUtils.toByteArray(request.getMessageID());
                    dataOutput.writeInt(requestMessageIDBytes.length);
                    dataOutput.write(requestMessageIDBytes);
                    byte[] operationIDBytes = MyByteUtils.toByteArray(request.getOperation().getOperationID());
                    dataOutput.writeInt(operationIDBytes.length);
                    dataOutput.write(operationIDBytes);
                    byte[] operationPathBytes = MyByteUtils.toByteArray(request.getOperation().getPath());
                    dataOutput.writeInt(operationPathBytes.length);
                    dataOutput.write(operationPathBytes);
                    if(request.getOperation().getOperationID() == 1) {
                        byte[] operationFile = request.getOperation().getFile();
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
                    byte[] clientIDBytes = MyByteUtils.toByteArray(request.getClientID());
                    dataOutput.writeInt(clientIDBytes.length);
                    dataOutput.write(clientIDBytes);
                    byte[] requestNumberBytes = MyByteUtils.toByteArray(request.getRequestNumber());
                    dataOutput.writeInt(requestNumberBytes.length);
                    dataOutput.write(requestNumberBytes);
                    byte[] requestViewNumberBytes = MyByteUtils.toByteArray(request.getViewNumber());
                    dataOutput.writeInt(requestViewNumberBytes.length);
                    dataOutput.write(requestViewNumberBytes);
                    //END OF REQUEST --------------------------------------------------------------------
                    //opNumber
                    byte[] operationNumberBytes = MyByteUtils.toByteArray(log.get(i).getOperationNumber());
                    dataOutput.writeInt(operationNumberBytes.length);
                    dataOutput.write(operationNumberBytes);
                    //isCommited
                    byte[] isCommitedBytes = MyByteUtils.toByteArray(log.get(i).isIsCommited());
                    dataOutput.writeInt(isCommitedBytes.length);
                    dataOutput.write(isCommitedBytes);
                }
            } else {
                dataOutput.writeInt(0);
                dataOutput.writeInt(1);
                byte[] nullFile = new byte[1];
                nullFile[0] = 0;
                dataOutput.write(nullFile);
            }
            
            byte[] lastCommitedBytes = MyByteUtils.toByteArray(doViewChange.getLastCommited());
            dataOutput.writeInt(lastCommitedBytes.length);
            dataOutput.write(lastCommitedBytes);
            byte[] replicaIDBytes = MyByteUtils.toByteArray(doViewChange.getReplicaID());
            dataOutput.writeInt(replicaIDBytes.length);
            dataOutput.write(replicaIDBytes);
            
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
