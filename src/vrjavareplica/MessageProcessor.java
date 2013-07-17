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
    int replicaID;
    
    public MessageProcessor(Replica replica) {
        this.replica = replica;
        this.replicaID = replica.getReplicaID();
    }
    
    public synchronized void processMessage(int messageID, Object message, Socket clientSocket) {
        switch(messageID) {
            case Constants.REQUEST : 
                MessageRequest request = (MessageRequest) message;
                processMessage(request, clientSocket);
                break;
            case Constants.PREPARE : 
                MessagePrepare prepare = (MessagePrepare) message;
                processMessage(prepare);
                break;
            case Constants.PREPAREOK : 
                MessagePrepareOK prepareOK = (MessagePrepareOK) message;
                processMessage(prepareOK);
                break;
            case Constants.DOVIEWCHANGE : 
                MessageDoViewChange doViewChange = (MessageDoViewChange) message;
                processMessage(doViewChange);
                break;
            case Constants.STARTVIEW : 
                MessageStartView startView = (MessageStartView) message;
                processMessage(startView);
                break;
        }
    }
    
    private void processMessage(MessageRequest request, Socket clientSocket) {
        LogWriter.log(replicaID, "Processing REQUEST...");
        
        replica.incrementOpNumber();
        
        replica.getLog().addLast(new ReplicaLogEntry(request, replica.getOpNumber(), clientSocket));
        MessagePrepare prepare = new MessagePrepare(
                request,
                replica.getViewNumber(),
                replica.getOpNumber(),
                replica.getLastCommited()
                );
        sendMessage(prepare);        
    }
    
    private void processMessage(MessagePrepare prepare) {
//        try {
//            Thread.sleep(1000*(long)Math.random()*15);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
//        }
        LogWriter.log(replicaID, "Processing PREPARE...");
        restartTimeoutChecker();
        
        replica.getLog().addLast(new ReplicaLogEntry(prepare.getRequest(), prepare.getOperationNumber()));
        MessagePrepareOK prepareOK = new MessagePrepareOK(
                replica.getViewNumber(), 
                prepare.getOperationNumber(), 
                replicaID);
        sendMessage(prepareOK);
        
        processLastCommited(prepare.getLastCommited());
    }
    
    private void processMessage(MessagePrepareOK prepareOK) {
        LogWriter.log(replicaID, "Processing PREPAREOK...");
        ReplicaLogEntry entry = replica.getLog().findEntry(prepareOK.getOperationNumber());
        if(entry == null) {
            //this is late prepareOK, not needed
            LogWriter.log(replicaID, "PREPAREOK - no such entry!");
            return;
        } else {
            entry.increaseCommitsNumber();
            LogWriter.log(replicaID, 
                    "Operation " + entry.getOperationNumber() + " commits " 
                    + entry.getCommitsNumber() + "/" + (replica.getReplicaTable().size() - 1));
            if(isCommitsSufficient(entry)) {
                if(isFirstInLog(entry)) {
                    replica.executeRequest(entry);
                    replica.setLastCommited(prepareOK.getOperationNumber());
                } else {
                    entry.setIsCommited(true);
                }
            }
        }
    }
    
    private void processLastCommited(int lastCommited) {
        if(lastCommited > 0) {
            if(!replica.getIpAddress().equals(replica.getPrimary().getIpAddress()) 
            || replica.getPort() != replica.getPrimary().getPort()) {
                ReplicaLogEntry entry = replica.getLog().findEntry(lastCommited);
                if(isFirstInLog(entry)) {
                    replica.executeRequest(entry);
                } else {
                    entry.setIsCommited(true);
                }
                replica.setLastCommited(lastCommited);
            }
        }
    }
    
    private void processMessage(MessageDoViewChange doViewChange) {
        LogWriter.log(replicaID, "Processing DOVIEWCHANGE...");
        replica.increaseNumDoViewChangeReceived();
        replica.getReplicasLogs().add(doViewChange.getLog());
        if(replica.getMostRecentDoViewChange() == null) {
            replica.setMostRecentDoViewChange(doViewChange);
        } else if(doViewChange.isLogMoreRecent(replica.getMostRecentDoViewChange())) {
            replica.setMostRecentDoViewChange(doViewChange);
        }
        if(isNumDoViewChangeReceivedSufficient()) {
            replica.switchToPrimary();
        }
    }
    
    private void processMessage(MessageStartView startView) {
        LogWriter.log(replicaID, "Processing STARTVIEW...");
        replica.setLog(startView.getLog());
        if(startView.getLog().size() > 0) {
            replica.setOpNumber(startView.getLog().peekLast().getOperationNumber());
        }
        replica.setViewNumber(startView.getViewNumber());
        replica.setState(Replica.ReplicaState.Normal);
        int nextPrimaryTableRow = (startView.getViewNumber() - 1) % replica.getReplicaTable().size();
        replica.setPrimary(new ReplicaInfo(
                replica.getReplicaTable().get(nextPrimaryTableRow).getReplicaID(),
                replica.getReplicaTable().get(nextPrimaryTableRow).getIpAddress(),
                replica.getReplicaTable().get(nextPrimaryTableRow).getPort()));
        LogWriter.log(replicaID, "View changed");
        LogWriter.log(replicaID, replica.getStatus());
        replica.startTimoutChecker();
        // send prepareOK to all
        
        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendMessage(MessageReply reply, Socket clientSocket) {
        DataOutputStream dataOutput = null;
        try {
            LogWriter.log(replicaID, "Sending message:" + Constants.NEWLINE + reply.toString());
            
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
                if(dataOutput != null) {
                    dataOutput.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sendMessage(MessagePrepare prepare) {
        for(int i = 0; i < replica.getReplicaTable().size(); i++) {
            if(i+1 != replicaID) { //not sending to primary
                new Thread(new ReplicaClientRunnable(
                        replica,
                        replica.getReplicaTable().get(i).getIpAddress(), 
                        replica.getReplicaTable().get(i).getPort(), 
                        Constants.PREPARE, 
                        prepare,
                        i+1)
                    ).start();
            }
        }
    }
    
    private void sendMessage(MessagePrepareOK prepareOK) {
        new Thread(new ReplicaClientRunnable(
                replica,
                replica.getPrimary().getIpAddress(), 
                replica.getPrimary().getPort(), 
                Constants.PREPAREOK, 
                prepareOK,
                replica.getPrimary().getReplicaID())
            ).start();
    }
    
    public void sendMessage(MessageDoViewChange doViewChange) {
        new Thread(new ReplicaClientRunnable(
                replica,
                replica.nextPrimary().getIpAddress(), 
                replica.nextPrimary().getPort(), 
                Constants.DOVIEWCHANGE, 
                doViewChange,
                replica.nextPrimary().getReplicaID())
            ).start();
    }
    
    public void sendMessage(MessageStartView startView) {
        for(int i = 0; i < replica.getReplicaTable().size(); i++) {
            if(i+1 != replicaID) { //not sending to primary
                new Thread(new ReplicaClientRunnable(
                        replica,
                        replica.getReplicaTable().get(i).getIpAddress(), 
                        replica.getReplicaTable().get(i).getPort(), 
                        Constants.STARTVIEW, 
                        startView,
                        i+1)
                    ).start();
            }
        }
    }
    
    private boolean isCommitsSufficient( ReplicaLogEntry entry ) {
        if(entry.isIsCommited()) {
            return true;
        } else {
            //check if number of commits is sufficient
            boolean isCommitsSufficient = (entry.getCommitsNumber()+1) > (replica.getReplicaTable().size()/2);
            if(isCommitsSufficient) {
                entry.setIsCommited(true);
            }
            return isCommitsSufficient;
        }
    }
    
    private boolean isFirstInLog(ReplicaLogEntry entry) {
        if(entry == null) {
            return false;
        } else {
            boolean isFirst = entry.equals(replica.getLog().getFirst());
            return isFirst;
        }
    }
    
    private void restartTimeoutChecker() {
        replica.getTimeoutChecker().restart();
    }
    
    private boolean isNumDoViewChangeReceivedSufficient() {
        boolean result = (replica.getNumDoViewChangeReceived() + 1) > (replica.getReplicaTable().size()/2);
        return result;
    }
}
