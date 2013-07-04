/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class Operation {
    private int operationID;
    private String path;
    private byte[] file;
    
    public Operation(String path, byte[] file) {
        operationID = 1;
        this.path = path;
        this.file = file;
    }
    
    public Operation( String path ) {
        operationID = 2;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOperationID() {
        return operationID;
    }

    public void setOperationID(int operationID) {
        this.operationID = operationID;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
    
    
}
