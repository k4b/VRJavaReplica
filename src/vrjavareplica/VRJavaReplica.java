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
public class VRJavaReplica {
    
    private static final String HOSTS_FILE_NAME = "Hosts.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startTestingReplicas(3);
//        startReplica();
    }
    
    private static void startTestingReplicas(int number) {
        ArrayList<ArrayList<String>> tokenizedLines = MyFileUtils.loadFile(HOSTS_FILE_NAME);
        for(int i = 0; i < number; i++) {
            new Replica(i+1, tokenizedLines.get(i).get(0), new Integer(tokenizedLines.get(i).get(1)));
        }
    }
    
    private static void startReplica() {
        Replica replica = new Replica();
    }
}
