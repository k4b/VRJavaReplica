/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class LogWriter {
    
    public static void log(int id, String text) {
        String s = "[Replica " + id + "] ";
        s += text + Constants.NEWLINE;
        s += "------------------------" + Constants.NEWLINE;
        System.out.println(s);
    }
}
