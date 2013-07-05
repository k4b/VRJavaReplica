/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

/**
 *
 * @author karol
 */
public class Constants {
    
    //Environment constants
    public static final String NEWLINE = System.getProperty("line.separator");
    
    //Files
    public static final String PARAMETER_FILE_NAME = "Parameters.txt";
    public static final String HOSTS_FILE_NAME = "Hosts.txt";
    public static final String FILE_DIRECTORY = "/home/karol/Documents/";
    
    //Message IDs
    public static final int REQUEST = 1;
    public static final int PREPARE = 2;
    public static final int PREPAREOK = 3;
    public static final int REPLY = 4;
    public static final int PING = 5;
    public static final int DOVIEWCHANGE = 6;
    public static final int STARTVIEW = 7;
    public static final int RECOVERY = 8;
    public static final int RECOVERYRESPONSE = 9;
    public static final int CORRECTVIEWNUMBER = 10;
    public static final int COMMIT = 11;
    
    //Operations
    public static final int COPY = 1;
    public static final int DELETE = 2;
    
}
