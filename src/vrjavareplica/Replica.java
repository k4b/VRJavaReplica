/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author karol
 */
public class Replica implements TimeoutListener {

    public enum ReplicaState { Normal, ViewChange, Recovering };  
    
    private int replicaID;
    private String ipAddress;
    private int port;
    private ReplicaInfo primary;
    private ReplicaTable replicaTable;
    private int viewNumber;
    private ReplicaState state;
    private int opNumber;
    private ClientTable clientTable;
    private ReplicaLog log;
    private int lastCommited;
    private MessageProcessor messageProcessor;
    private TimeoutChecker timeoutChecker;;
    private ReplicaLog executedRequests;
    private int numDoViewChangeReceived;
    private MessageDoViewChange mostRecentDoViewChange = null;
    private ArrayList<ReplicaLog> replicasLogs = new ArrayList<>();
    private int timeout = 20;
    
    public Replica() {
        replicaTable = new ReplicaTable();
        clientTable = new ClientTable();
        log = new ReplicaLog();
        executedRequests = new ReplicaLog();
        loadAndSetParameters();
        LogWriter.log(replicaID, identify());
        LogWriter.log(replicaID, getStatus());
        
        if(!isPrimary()) {
            startTimoutChecker();
        }
        messageProcessor = new MessageProcessor(this);
        VRCode vrCode = new VRCode(replicaID, port, messageProcessor);
        new Thread(vrCode).start();
    }
    
    public Replica(int id, String address, int port) {
        replicaTable = new ReplicaTable();
        clientTable = new ClientTable();
        log = new ReplicaLog();
        executedRequests = new ReplicaLog();
        loadHosts();
        loadClients();
        this.replicaID = id;
        this.ipAddress = address;
        this.port = port;
        primary = replicaTable.get(0);
        viewNumber = 1;
        opNumber = 0;
        state = ReplicaState.Normal;
        lastCommited = 0;
        numDoViewChangeReceived = 0;
        LogWriter.log(replicaID, identify());
        LogWriter.log(replicaID, getStatus());
        
        if(!isPrimary()) {
            startTimoutChecker();
        }
        messageProcessor = new MessageProcessor(this);
        VRCode vrCode = new VRCode(replicaID, port, messageProcessor);
        new Thread(vrCode).start();
    }
    
    public void startTimoutChecker() {
            timeoutChecker = new TimeoutChecker(timeout);
            timeoutChecker.addTimeoutListener(this);
            timeoutChecker.start();
    }
    
    private void stopTimeoutChecker() {
        timeoutChecker.terminate();
    }
    public String identify() {    

        String s = "Identification:" + Constants.NEWLINE;
        s += "ipAddress: " + ipAddress + Constants.NEWLINE;
        s += "port: " + port + Constants.NEWLINE;
        s += "Hosts: " + Constants.NEWLINE;
        for(ReplicaInfo rep : replicaTable) {
            s += "Replica[" + rep.getReplicaID() + "] " + rep.getIpAddress() + ":" + rep.getPort() + Constants.NEWLINE;
        }
        return s;
    }
    
    public String getStatus() {
        String status = "Status:" + Constants.NEWLINE;
        status += "View number " + viewNumber + Constants.NEWLINE;
        status += "Operation number: " + opNumber + Constants.NEWLINE;
        status += "State: " + state.toString() + Constants.NEWLINE;
        status += "Is primary: " + isPrimary() + Constants.NEWLINE;
        return status;
    }
    
    private void loadAndSetParameters() {
        loadHosts();
        loadClients();
        loadParameters();
        
        primary = replicaTable.get(0);
        viewNumber = 1;
        opNumber = 0;
        state = ReplicaState.Normal;
        lastCommited = 0;
        numDoViewChangeReceived = 0;
    }
           
    private boolean loadHosts() {
        ArrayList<ArrayList<String>> tokenizedLines = MyFileUtils.loadFile(Constants.HOSTS_FILE_NAME);
        if(tokenizedLines.size() > 0) {
            for(int i = 0; i < tokenizedLines.size(); i++) {
                ArrayList<String> tokens = tokenizedLines.get(i);
                if(tokens.size() >= 2) {
                    int replicaID = i+1;
                    String address = tokens.get(0);                
                    int port = Integer.valueOf(tokens.get(1));
                    replicaTable.add(new ReplicaInfo(replicaID, address, port));
                }
            }
        } 
        boolean result = (replicaTable.size() > 0) ? true : false;
        return result;
    }
    
    public boolean  testloadHosts() {
        return loadHosts();
    }
    
    private boolean loadClients() {
        ArrayList<ArrayList<String>> tokenizedLines = MyFileUtils.loadFile(Constants.CLIENTS_FILE_NAME);
        if(tokenizedLines.size() > 0) {
            for(int i = 0; i < tokenizedLines.size(); i++) {
                ArrayList<String> tokens = tokenizedLines.get(i);
                if(tokens.size() >= 2) {
                    int clientID = i+1;
                    String address = tokens.get(0);                
                    int port = Integer.valueOf(tokens.get(1));
                    clientTable.add(new ClientInfo(clientID, address, port));
                }
            }
        } 
        boolean result = (clientTable.size() > 0) ? true : false;
        return result;
    }
    
    private boolean loadParameters() {
        ArrayList<ArrayList<String>> tokenizedLines = MyFileUtils.loadFile(Constants.PARAMETER_FILE_NAME);
        if(tokenizedLines.size() >= 3) {
            int repID = Integer.valueOf(tokenizedLines.get(0).get(0));
            String address = tokenizedLines.get(1).get(0);
            int portNumber = Integer.valueOf(tokenizedLines.get(2).get(0));
            this.replicaID = repID;
            this.ipAddress = address;
            this.port = portNumber;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean  testLoadParameters() {
        return loadParameters();
    }
    
    public int getReplicaID() {
        return replicaID;
    }

    public void setReplicaID(int replicaID) {
        this.replicaID = replicaID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ReplicaInfo getPrimary() {
        return primary;
    }

    public void setPrimary(ReplicaInfo primary) {
        this.primary = primary;
    }

    public ReplicaTable getReplicaTable() {
        return replicaTable;
    }

    public void setReplicaTable(ReplicaTable replicaTable) {
        this.replicaTable = replicaTable;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }

    public ReplicaState getState() {
        return state;
    }

    public void setState(ReplicaState state) {
        this.state = state;
    }

    public int getOpNumber() {
        return opNumber;
    }

    public void setOpNumber(int opNumber) {
        this.opNumber = opNumber;
    }

    public ClientTable getClientTable() {
        return clientTable;
    }

    public void setClientTable(ClientTable clientTable) {
        this.clientTable = clientTable;
    }

    public ReplicaLog getLog() {
        return log;
    }

    public void setLog(ReplicaLog log) {
        this.log = log;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public int getLastCommited() {
        return lastCommited;
    }

    public void setLastCommited(int lastCommited) {
        this.lastCommited = lastCommited;
    }
    
    public TimeoutChecker getTimeoutChecker() {
        return timeoutChecker;
    }

    public int getNumDoViewChangeReceived() {
        return numDoViewChangeReceived;
    }
    
    public void increaseNumDoViewChangeReceived() {
        numDoViewChangeReceived++;
    }

    public MessageDoViewChange getMostRecentDoViewChange() {
        return mostRecentDoViewChange;
    }

    public void setMostRecentDoViewChange(MessageDoViewChange mostRecentDoViewChange) {
        this.mostRecentDoViewChange = mostRecentDoViewChange;
    }

    public int getTimeout() {
        return timeout;
    }

    public ArrayList<ReplicaLog> getReplicasLogs() {
        return replicasLogs;
    }
    
    public void incrementOpNumber() {
        opNumber++;
    }
    
    
    
    
    
    public synchronized boolean executeRequest(ReplicaLogEntry entry) {
        int operation = entry.getRequest().getOperation().getOperationID();
        boolean result = false;
        switch(operation) {
            case Constants.COPY : 
                result = executeCopy(entry.getRequest().getOperation());
                break;
            case Constants.DELETE : 
                result = executeDelete(entry.getRequest().getOperation());
                break;
        }
        
        if(result) {
            executedRequests.addLast(entry);
            try {
                ReplicaLogEntry firstEntry = log.peek(); //retrieves but doesn't remove first element in the list
                if(entry.equals(firstEntry)) {
                    log.removeFirst();
                }
                if(isPrimary()) {
                    MessageReply reply = new MessageReply(this.getViewNumber(), entry.getRequest().getRequestNumber(), result);
                    messageProcessor.sendMessage(reply, entry.getRequest().getClientID());
                }
                ReplicaLogEntry nextEntry = log.peek(); //retrieves but doesn't remove first element in the list
                if(nextEntry != null && nextEntry.isIsCommited()) {
                    executeRequest(nextEntry);
                }
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    private boolean executeCopy(Operation operation) {
        String path = replicaID + "-" + operation.getPath();
        boolean result = MyFileUtils.saveFile(path, operation.getFile());
        LogWriter.log(replicaID, "Executed copy file " + operation.getPath() + " with result = " + result);
        return result;
    }
    
    private boolean executeDelete(Operation operation) {
        String path = replicaID + "-" + operation.getPath();
        boolean result = MyFileUtils.deleteFile(path);
        LogWriter.log(replicaID, "Executed delete file " + operation.getPath() + " with result = " + result);
        return result;
    }
    
    public boolean isPrimary() {
        if(this.ipAddress.equals(this.primary.getIpAddress()) && this.port == this.primary.getPort()) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isNextPrimary() {
        //viewNumber start from 1, positions from 0
        ReplicaInfo thisReplicaInfo = new ReplicaInfo(replicaID, ipAddress, port);
        if(viewNumber == positionInReplicasTable(thisReplicaInfo)) {
            return true;
        } else {
            return false;
        }
    }
    
    public ReplicaInfo nextPrimary() {
        int position = positionInReplicasTable(primary);
        if(position == replicaTable.size()-1) {
            position = -1;
        }
        ReplicaInfo nextPrimary = replicaTable.get(position+1);
        return nextPrimary;
    }
    
    private int positionInReplicasTable(ReplicaInfo replicaInfo) {
        int position = -1;
        for(int i = 0; i < replicaTable.size(); i++) {
            if(replicaTable.get(i).getIpAddress().equals(replicaInfo.getIpAddress()) 
                            && replicaTable.get(i).getPort() == replicaInfo.getPort()) {
                position = i;
                break;
            }
        }
        if(position == -1) {
            throw new RuntimeException("Replica " + replicaInfo.getReplicaID() + " " + ipAddress 
                    + ":" + port + " does not appear in replicas table!");
        }
        return position;
    }
    
    @Override
    public void timeout() {
        if(!isPrimary()) {
            LogWriter.log(replicaID, "Primary timeout!");
            carryViewChange();
        }
    }
    
    public void carryViewChange() {
        if(!isNextPrimary()) {
            viewNumber++;
            state = ReplicaState.ViewChange;
            MessageDoViewChange doViewChange = new MessageDoViewChange(replicaID, viewNumber, log, lastCommited);
            messageProcessor.sendMessage(doViewChange);
        }
    }
    
    public void switchToPrimary() {
        stopTimeoutChecker();
        if(mostRecentDoViewChange.getLog().size() == 0) {
            opNumber = 0;
        } else {
            opNumber = mostRecentDoViewChange.getLog().getLast().getOperationNumber();
        }
        viewNumber = mostRecentDoViewChange.getViewNumber();
        state = ReplicaState.Normal;
        primary = new ReplicaInfo(this.replicaID, this.ipAddress, this.port);
        numDoViewChangeReceived = 0;
        LogWriter.log(replicaID, "Switched to PRIMARY");
        LogWriter.log(replicaID, getStatus());
        MessageStartView startView = new MessageStartView(viewNumber, mostRecentDoViewChange.getLog(), opNumber);
        messageProcessor.sendMessage(startView);
        //execute pending operations
        ArrayList<ReplicaLogEntry> entries = computeOperationsToExecute(replicasLogs, replicaTable.size()/2);
        for(ReplicaLogEntry entry : entries) {
            executeRequest(entry);
            //send replies to clients.
        }
    }
    
    private ArrayList<ReplicaLogEntry> computeOperationsToExecute(ArrayList<ReplicaLog> logList, int treshold) {
        ArrayList<Viewstamp> toExecute = new ArrayList<>();
        ViewstampMap map = new ViewstampMap();
        HashMap<Viewstamp, ReplicaLogEntry> logEntryMap = new HashMap<>();
        ArrayList<ReplicaLogEntry> entries = new ArrayList<>();
        
        for(int i = 0; i < logList.size(); i++) {
            for(int j = 0 ; j < logList.get(i).size(); j++) {
                ReplicaLogEntry entry = logList.get(i).get(j);
                int viewNumber = entry.getRequest().getViewNumber();
                int operationNumber = entry.getOperationNumber();
                Viewstamp viewstamp = new Viewstamp(viewNumber, operationNumber);
                map.add(viewstamp);
                logEntryMap.put(viewstamp, entry);
                
            }
        }
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if((int)pairs.getValue() >= treshold) {
                toExecute.add((Viewstamp)pairs.getKey());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        sortViewstamps(toExecute);
        for(int k = 0; k < toExecute.size(); k++) {
            entries.add(logEntryMap.get(toExecute.get(k)));
        }
        
        return entries;
    }
    
    private void sortViewstamps(ArrayList<Viewstamp> list) {
        //Bubble sort
        Viewstamp temp = null;
        for(int i = 0; i < list.size(); i++) {
            for(int j = 1; j < list.size()-i; j++) {
                if(list.get(j-1).isGreater(list.get(j))) {
                    temp =  list.get(j-1);
                    list.set(j-1, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }
    
    
}
