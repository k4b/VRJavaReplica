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
            case 1 : 
                MessageRequest request = (MessageRequest) message;
                processRequest(request, clientSocket);
                break;
        }
    }
    
    private void processRequest(MessageRequest request, Socket clientSocket) {
        LogWriter.log(replica.getReplicaID(), "Processing request...");
        MessageReply reply = new MessageReply(4, request.getViewNumber(), request.getRequestNumber(), true);
        sendMessage(reply, clientSocket);
    }
    
    private void sendMessage(MessageReply reply, Socket clientSocket) {
        DataOutputStream dataOutput = null;
        try {
            LogWriter.log(replica.getReplicaID(), "Sending message:");
            LogWriter.log(replica.getReplicaID(), reply.toString());
            
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
}
