/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

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
    
    public synchronized void processMessage(int messageID, Object message) {
        switch(messageID) {
            case Constants.REQUEST : 
                MessageRequest request = (MessageRequest) message;
                processMessage(request);
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
    
    private void processMessage(MessageRequest request) {
        LogWriter.log(replicaID, "Processing REQUEST...");
        if(replica.isPrimary()) {
            replica.incrementOpNumber();
            replica.getLog().addLast(new ReplicaLogEntry(request, replica.getOpNumber()));
            MessagePrepare prepare = new MessagePrepare(
                    request,
                    replica.getViewNumber(),
                    replica.getOpNumber(),
                    replica.getLastCommited()
                    );
            sendMessage(prepare);
        }
    }
    
    private void processMessage(MessagePrepare prepare) {
        LogWriter.log(replicaID, "Processing PREPARE...");
        if(replica.getViewNumber() == prepare.getViewNumber()) {
            if(replica.getState().equals(Replica.ReplicaState.Normal)) {
                restartTimeoutChecker();
                replica.getLog().addLast(new ReplicaLogEntry(prepare.getRequest(), prepare.getOperationNumber()));
                MessagePrepareOK prepareOK = new MessagePrepareOK(
                        replica.getViewNumber(), 
                        prepare.getOperationNumber(), 
                        replicaID);
                sendMessage(prepareOK);

                processLastCommited(prepare.getLastCommited());
            } else {
                LogWriter.log(replicaID, "State: " + replica.getState());
            }
        } else {
            processWrongViewNumber(prepare.getMessageID(), prepare);
        }
    }
    
    private void processMessage(MessagePrepareOK prepareOK) {
        LogWriter.log(replicaID, "Processing PREPAREOK...");
        if(replica.getViewNumber() == prepareOK.getViewNumber()) {
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
        } else {
            processWrongViewNumber(prepareOK.getMessageID(), prepareOK);
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
        if(replica.getViewNumber() <= doViewChange.getViewNumber()) {
            replica.increaseNumDoViewChangeReceived();
            replica.getReplicasLogs().add(doViewChange.getLog());
            if(replica.getMostRecentDoViewChange() == null) {
                replica.setMostRecentDoViewChange(doViewChange);
            } else if(doViewChange.getLog().isMoreRecent(replica.getMostRecentDoViewChange().getLog())) {
                replica.setMostRecentDoViewChange(doViewChange);
            }
            if(isNumDoViewChangeReceivedSufficient()) {
                replica.switchToPrimary();
            }
        } else {
            processWrongViewNumber(doViewChange.getMessageID(), doViewChange);
        }
    }
    
    private void processMessage(MessageStartView startView) {
        LogWriter.log(replicaID, "Processing STARTVIEW...");
        if(replica.getViewNumber() <= startView.getViewNumber()) {
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
            commitNotCommited(startView);
        } else {
            processWrongViewNumber(startView.getMessageID(), startView);
        }
    }
    
    private void commitNotCommited(MessageStartView startView) {
        int num = replica.getOpNumber() - startView.getLastCommited();
        ReplicaLogEntry e = replica.getLog().findEntry(startView.getLastCommited());
        int index = replica.getLog().indexOf(e);
        for(int i = 0; i < num ; i++) {
            ReplicaLogEntry entry = replica.getLog().get(index+1+i);
            MessagePrepareOK prepareOK = new MessagePrepareOK(
                    replica.getViewNumber(), 
                    entry.getOperationNumber(), 
                    replicaID);
            sendMessage(prepareOK);
        }
    }
    
    private void processWrongViewNumber(int messageID, Message message) {
        MessageCorrectViewNumber correct = new MessageCorrectViewNumber(replica.getViewNumber());
        switch(messageID) {
            case Constants.REQUEST : 
                MessageRequest request = (MessageRequest) message;
                int index = request.getClientID()-1;
                sendMessage(
                        correct,
                        replica.getClientTable().get(index).getIpAddress(),
                        replica.getClientTable().get(index).getPort(),
                        request.getClientID());
                break;
            case Constants.PREPARE : 
                MessagePrepare prepare = (MessagePrepare) message;
                index = (prepare.getViewNumber()-1) % replica.getReplicaTable().size();
                sendMessage(
                        correct,
                        replica.getReplicaTable().get(index).getIpAddress(),
                        replica.getReplicaTable().get(index).getPort(),
                        replica.getReplicaTable().get(index).getReplicaID());
                break;
            case Constants.PREPAREOK : 
                MessagePrepareOK prepareOK = (MessagePrepareOK) message;
                index = (prepareOK.getReplicaID()-1) % replica.getReplicaTable().size();
                sendMessage(
                        correct,
                        replica.getReplicaTable().get(index).getIpAddress(),
                        replica.getReplicaTable().get(index).getPort(),
                        replica.getReplicaTable().get(index).getReplicaID());
                break;
            case Constants.DOVIEWCHANGE : 
                MessageDoViewChange doViewChange = (MessageDoViewChange) message;
                index = (doViewChange.getReplicaID()-1) % replica.getReplicaTable().size();
                sendMessage(
                        correct,
                        replica.getReplicaTable().get(index).getIpAddress(),
                        replica.getReplicaTable().get(index).getPort(),
                        replica.getReplicaTable().get(index).getReplicaID());
                break;
        }
    }
    
    public void sendMessage(MessageReply reply, int clientID) {
        int index = clientID - 1;
        ClientInfo cInfo = replica.getClientTable().get(index);
        new Thread(new ReplicaClientRunnable(
                replica,
                cInfo.getIpAddress(), 
                cInfo.getPort(), 
                Constants.REPLY, 
                reply,
                clientID)
            ).start();
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
    
    public void sendMessage(MessageCorrectViewNumber correctViewNumber, String ipAddress, int port, int receiverID) {
        new Thread(new ReplicaClientRunnable(
                replica,
                ipAddress, 
                port, 
                Constants.CORRECTVIEWNUMBER, 
                correctViewNumber,
                receiverID)
            ).start();
    }
    
    private boolean isCommitsSufficient( ReplicaLogEntry entry ) {
        if(entry.isCommited()) {
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
