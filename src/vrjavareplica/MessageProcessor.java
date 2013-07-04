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
public class MessageProcessor {
    
    Replica replica;
    
    public MessageProcessor(Replica replica) {
        this.replica = replica;
    }
    
    public void processMessage(int messageID, Object message, Socket clientSocket) {
        switch(messageID) {
            case Constants.REQUEST : 
                MessageRequest request = (MessageRequest) message;
                processRequest(request, clientSocket);
                break;
            case Constants.PREPARE : 
                MessagePrepare prepare = (MessagePrepare) message;
                processPrepare(prepare, clientSocket);
                break;
        }
    }
    
    private void processRequest(MessageRequest request, Socket clientSocket) {
        LogWriter.log(replica.getReplicaID(), "Processing REQUEST...");
        
        replica.incrementOpNumber();
        replica.getLog().addLast(request);
        MessagePrepare prepare = new MessagePrepare(
                request,
                replica.getViewNumber(),
                replica.getOpNumber(),
                replica.getLastCommited()
                );
        sendMessage(prepare);
        
        //To powinno być  w przetwarzaniu PREPAREOK
//        MessageReply reply = new MessageReply(4, request.getViewNumber(), request.getRequestNumber(), true);
//        sendMessage(reply, clientSocket);
    }
    
    private void processPrepare(MessagePrepare prepare, Socket clientSocket) {
        LogWriter.log(replica.getReplicaID(), "Processing PREPARE...");
    }
    
    private void sendMessage(MessageReply reply, Socket clientSocket) {
        DataOutputStream dataOutput = null;
        try {
            LogWriter.log(replica.getReplicaID(), "Sending message:" + Constants.NEWLINE + reply.toString());
            
            dataOutput = new DataOutputStream(clientSocket.getOutputStream());
            byte[] messageIDBytes = MyByteUtils.toByteArray(reply.getMessageID());
            dataOutput.writeInt(messageIDBytes.length);
            dataOutput.write(messageIDBytes);
            byte[] viewNumberBytes = MyByteUtils.toByteArray(reply.getViewNumber());
            dataOutput.writeInt(viewNumberBytes.length);
            dataOutput.write(viewNumberBytes);
            byte[] requestNumberBytes = MyByteUtils.toByteArray(reply.getRequestNumber());
            dataOutput.writeInt(requestNumberBytes.length);
            dataOutput.write(requestNumberBytes);
            byte[] resultBytes = MyByteUtils.toByteArray(reply.getResult());
            dataOutput.writeInt(resultBytes.length);
            dataOutput.write(resultBytes);
            
        } catch (IOException ex) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dataOutput.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sendMessage(MessagePrepare prepare) {
        for(int i = 0; i < replica.getReplicaTable().size(); i++) {
            new Thread(new ReplicaClientRunnable(
                    replica,
                    replica.getReplicaTable().get(i).getIpAddress(), 
                    replica.getReplicaTable().get(i).getPort(), 
                    2, 
                    prepare)
                ).start();
        }
        
    }
}
