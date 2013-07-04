/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vrjavareplica;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author karol
 */
public class ServerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    public ServerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {            
            System.out.println("Collecting message");
            DataInputStream dataInput = new DataInputStream(clientSocket.getInputStream());
            int size = dataInput.readInt();
            byte[] requestIDBytes = new byte[size];
            dataInput.read(requestIDBytes, 0, size);
            size = dataInput.readInt();
            byte[] operationIDBytes = new byte[size];
            dataInput.read(operationIDBytes, 0, size);
            size = dataInput.readInt();
            byte[] operationPathBytes = new byte[size];
            dataInput.read(operationPathBytes, 0, size);
            byte[] operationFile = null;
            if(byteArrayToInt(operationIDBytes) == 1) {
                size = dataInput.readInt();
                if(size != 1) {
                    operationFile = new byte[size];
                    if(operationFile != null) {
                        dataInput.read(operationPathBytes, 0, size);
                    }
                } else {
                    dataInput.read();
                }
            }
            size = dataInput.readInt();
            byte[] clientIDBytes = new byte[size];
            dataInput.read(clientIDBytes, 0, size);
            size = dataInput.readInt();
            byte[] requestNumberBytes = new byte[size];
            dataInput.read(requestNumberBytes, 0, size);
            size = dataInput.readInt();
            byte[] viewNumberBytes = new byte[size];
            dataInput.read(viewNumberBytes, 0, size);
            
            
            Operation operation = null;
            int operationID = byteArrayToInt(operationIDBytes);
            switch(operationID) {
                case 1 :
                    operation = new Operation(
                            new String(operationPathBytes),
                            operationFile);
                    break;
                case 2 :
                    operation = new Operation( new String(operationPathBytes));
                    break;
            }

            MessageRequest request = new MessageRequest(
                    byteArrayToInt(requestIDBytes),
                    operation,
                    byteArrayToInt(clientIDBytes),
                    byteArrayToInt(requestNumberBytes),
                    byteArrayToInt(viewNumberBytes));

            System.out.println("Received request:");
            System.out.println(request.toString());
            
            {
                //process request
            }
            
            MessageReply reply = new MessageReply(4, request.getViewNumber(), request.getRequestNumber(), true);
            sendMessage(reply);
            
            dataInput.close();
            clientSocket.close();
            
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    
    private void sendMessage(MessageReply reply) {
        DataOutputStream dataOutput = null;
        try {
            System.out.println("Sending message:");
            System.out.println(reply.toString());
            
            dataOutput = new DataOutputStream(clientSocket.getOutputStream());
            byte[] messageIDBytes = toByteArray(reply.getMessageID());
            dataOutput.writeInt(messageIDBytes.length);
            dataOutput.write(messageIDBytes);
            byte[] viewNumberBytes = toByteArray(reply.getViewNumber());
            dataOutput.writeInt(viewNumberBytes.length);
            dataOutput.write(viewNumberBytes);
            byte[] requestNumberBytes = toByteArray(reply.getRequestNumber());
            dataOutput.writeInt(requestNumberBytes.length);
            dataOutput.write(requestNumberBytes);
            byte[] resultBytes = toByteArray(reply.getResult());
            dataOutput.writeInt(resultBytes.length);
            dataOutput.write(resultBytes);
            
            dataOutput.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dataOutput.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static byte[] toByteArray(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        //buffer.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        buffer.putInt(value);
        byte[] result = buffer.array();
        return result;
    }
    
    public static byte[] toByteArray(String text) {
        return text.getBytes();
    }
    
    public static byte[] toByteArray(boolean value) {
        return new byte[]{(byte) (value ? 1 : 0)};
    }
    
    public static int byteArrayToInt(byte[] bytes) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        //bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }
    
    public static boolean byteArrayToBoolean(byte[] bytes) {
        return (bytes[0]!=0);
    }
}