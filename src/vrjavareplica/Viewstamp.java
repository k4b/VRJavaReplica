/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author Karol
 */
public class Viewstamp {
    
    private int viewNumber;
    private int operationNumber;
    
    public Viewstamp(int viewNumber, int operationNumber) {
        this.viewNumber = viewNumber;
        this.operationNumber = operationNumber;
    }

    public int getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(int operationNumber) {
        this.operationNumber = operationNumber;
    }

    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {
        this.viewNumber = viewNumber;
    }
    
    public boolean isGreater(Viewstamp compared) {
        boolean result = false;
        if(this.viewNumber > compared.getViewNumber()) {
            result = true;
        } else if (this.viewNumber == compared.getViewNumber()
                && this.operationNumber > compared.getOperationNumber()) {
            result = true;
        }
        return result;
    }
}
