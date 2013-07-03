/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author karol
 */
public class ReplicaTest {
    
    public ReplicaTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of LoadParameters method, of class Replica.
     */
    @Test
    public void testLoadParameters() {
        System.out.println("LoadParameters");
        Replica instance = new Replica();
        assertEquals(true, instance.getReplicaID() != 0);
    }
    
    /**
     * Test of LoadHosts method, of class Replica.
     */
    @Test
    public void testLoadHosts() {
        System.out.println("LoadHosts");
        Replica instance = new Replica();
        assertEquals(true, instance.getReplicaTable().size() > 0);
    }
    
    /**
     * Test of getReplicaID method, of class Replica.
     */
    //@Test
    public void testGetReplicaID() {
        System.out.println("getReplicaID");
        Replica instance = new Replica();
        int expResult = 0;
        int result = instance.getReplicaID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setReplicaID method, of class Replica.
     */
    //@Test
    public void testSetReplicaID() {
        System.out.println("setReplicaID");
        int replicaID = 0;
        Replica instance = new Replica();
        instance.setReplicaID(replicaID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIpAddress method, of class Replica.
     */
    //@Test
    public void testGetIpAddress() {
        System.out.println("getIpAddress");
        Replica instance = new Replica();
        String expResult = "";
        String result = instance.getIpAddress();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIpAddress method, of class Replica.
     */
    //@Test
    public void testSetIpAddress() {
        System.out.println("setIpAddress");
        String ipAddress = "";
        Replica instance = new Replica();
        instance.setIpAddress(ipAddress);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPrimary method, of class Replica.
     */
    //@Test
    public void testGetPrimary() {
        System.out.println("getPrimary");
        Replica instance = new Replica();
        ReplicaInfo expResult = null;
        ReplicaInfo result = instance.getPrimary();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPrimary method, of class Replica.
     */
    //@Test
    public void testSetPrimary() {
        System.out.println("setPrimary");
        ReplicaInfo primary = null;
        Replica instance = new Replica();
        instance.setPrimary(primary);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReplicaTable method, of class Replica.
     */
    //@Test
    public void testGetReplicaTable() {
        System.out.println("getReplicaTable");
        Replica instance = new Replica();
        ReplicaTable expResult = null;
        ReplicaTable result = instance.getReplicaTable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setReplicaTable method, of class Replica.
     */
    //@Test
    public void testSetReplicaTable() {
        System.out.println("setReplicaTable");
        ReplicaTable replicaTable = null;
        Replica instance = new Replica();
        instance.setReplicaTable(replicaTable);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getViewNumber method, of class Replica.
     */
    //@Test
    public void testGetViewNumber() {
        System.out.println("getViewNumber");
        Replica instance = new Replica();
        int expResult = 0;
        int result = instance.getViewNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setViewNumber method, of class Replica.
     */
    //@Test
    public void testSetViewNumber() {
        System.out.println("setViewNumber");
        int viewNumber = 0;
        Replica instance = new Replica();
        instance.setViewNumber(viewNumber);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getState method, of class Replica.
     */
    //@Test
    public void testGetState() {
        System.out.println("getState");
        Replica instance = new Replica();
        Replica.ReplicaState expResult = null;
        Replica.ReplicaState result = instance.getState();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setState method, of class Replica.
     */
    //@Test
    public void testSetState() {
        System.out.println("setState");
        Replica.ReplicaState state = null;
        Replica instance = new Replica();
        instance.setState(state);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOpNumber method, of class Replica.
     */
    //@Test
    public void testGetOpNumber() {
        System.out.println("getOpNumber");
        Replica instance = new Replica();
        int expResult = 0;
        int result = instance.getOpNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setOpNumber method, of class Replica.
     */
    //@Test
    public void testSetOpNumber() {
        System.out.println("setOpNumber");
        int opNumber = 0;
        Replica instance = new Replica();
        instance.setOpNumber(opNumber);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClientTable method, of class Replica.
     */
    //@Test
    public void testGetClientTable() {
        System.out.println("getClientTable");
        Replica instance = new Replica();
        ClientTable expResult = null;
        ClientTable result = instance.getClientTable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setClientTable method, of class Replica.
     */
    //@Test
    public void testSetClientTable() {
        System.out.println("setClientTable");
        ClientTable clientTable = null;
        Replica instance = new Replica();
        instance.setClientTable(clientTable);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLog method, of class Replica.
     */
    //@Test
    public void testGetLog() {
        System.out.println("getLog");
        Replica instance = new Replica();
        Log expResult = null;
        Log result = instance.getLog();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLog method, of class Replica.
     */
    //@Test
    public void testSetLog() {
        System.out.println("setLog");
        Log log = null;
        Replica instance = new Replica();
        instance.setLog(log);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPort method, of class Replica.
     */
    //@Test
    public void testGetPort() {
        System.out.println("getPort");
        Replica instance = new Replica();
        int expResult = 0;
        int result = instance.getPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPort method, of class Replica.
     */
    //@Test
    public void testSetPort() {
        System.out.println("setPort");
        int port = 0;
        Replica instance = new Replica();
        instance.setPort(port);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}