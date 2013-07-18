/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author Karol
 */
public class MessageCorrectViewNumber extends Message{
    
    int viewNumber;
    
    public MessageCorrectViewNumber(int viewNumber) {
        setMessageID(Constants.CORRECTVIEWNUMBER);
        this.viewNumber = viewNumber;
    }
}
