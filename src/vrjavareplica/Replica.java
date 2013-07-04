/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.util.ArrayList;

/**
 *
 * @author karol
 */
public class Replica {
    
    public enum ReplicaState { Normal, ViewChange, Recovering };
    private static final String PARAMETER_FILE_NAME = "Parameters.txt";
    private static final String HOSTS_FILE_NAME = "Hosts.txt";
    private static final String NEWLINE = System.getProperty("line.separator");
    
    
    private int replicaID;
    private String ipAddress;
    private int port;
    private ReplicaInfo primary;
    private ReplicaTable replicaTable;
    private int viewNumber;
    private ReplicaState state;
    private int opNumber;
    private ClientTable clientTable;
    private Log log;
    
    public Replica() {
        replicaTable = new ReplicaTable();
        clientTable = new ClientTable();
        loadParameters();
        loadHosts();
        primary = replicaTable.get(0);
        viewNumber = 0;
        opNumber = 0;
        log = new Log();
        System.out.println(identify());
        
        MessageProcessor messageProcessor = new MessageProcessor(this);
        VRCode vrCode = new VRCode(port, messageProcessor);
        new Thread(vrCode).start();
    }
    
    public String identify() {
        String s = "";
        s += "Replica App" + NEWLINE;
        s += "ID: " + replicaID + NEWLINE;
        s += "ipAddress: " + ipAddress + NEWLINE;
        s += "port: " + port + NEWLINE;
        s += "Hosts: " + NEWLINE;
        for(ReplicaInfo rep : replicaTable) {
            s += "Replica[" + rep.getReplicaID() + "] " + rep.getIpAddress() + ":" + rep.getPort() + NEWLINE;
        }
        return s;
    }
            
    private boolean loadParameters() {
        ArrayList<ArrayList<String>> tokenizedLines = FileUtility.loadFile(PARAMETER_FILE_NAME);
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
    
    private boolean loadHosts() {
        ArrayList<ArrayList<String>> tokenizedLines = FileUtility.loadFile(HOSTS_FILE_NAME);
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

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    
}
