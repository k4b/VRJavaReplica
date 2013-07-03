/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class ReplicaInfo {
    private int replicaID;
    private String ipAddress;
    private int port;
    
    public ReplicaInfo(int id, String address, int port) {
        this.replicaID = id;
        this.ipAddress = address;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
    
    
}
