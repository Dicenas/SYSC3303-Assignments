import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 * IntermediateHost class that creates a DatagramSocket (port 23) to use to receive
 * requests from the client. It creates a DatagramSocket (port 69) to relay the request from the
 * client to the server, and waits for a response from the server. After receiving
 * a response from the server, it then relays the response to the client.
 *
 * @author Daniel Godfrey
 */
public class IntermediateHost {
    private DatagramSocket receiveSocket, sendReceiveSocket;
    private byte[] buf = new byte[256];

    public IntermediateHost() throws SocketException {
        receiveSocket = new DatagramSocket(23); // Receive socket to receive initial request from client
        sendReceiveSocket = new DatagramSocket();
    }

    /**
     * Relay messages between the client and server.
     * @throws IOException
     */
    public void relay() throws IOException {
        byte[] receiveBuffer = new byte[1024]; // Use a sufficiently large buffer for incoming packets

        while (true) {
            // Receive from client
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            receiveSocket.receive(receivePacket);

            System.out.println("Intermediate Host - Received from Client (String): " + new String(receivePacket.getData(), 0, receivePacket.getLength()));
            System.out.println("Intermediate Host - Received from Client (Bytes): " + bytesToHex(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength())));

            // Forward to the server with data length
            DatagramPacket forwardPacketToServer = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), InetAddress.getByName("localhost"), 69);
            sendReceiveSocket.send(forwardPacketToServer);

            System.out.println("Intermediate Host - Sent to Server (String): " + new String(forwardPacketToServer.getData(), 0, forwardPacketToServer.getLength()));
            System.out.println("Intermediate Host - Sent to Server (Bytes): " + bytesToHex(Arrays.copyOf(forwardPacketToServer.getData(), forwardPacketToServer.getLength())));

            // Receive response from server
            DatagramPacket responseFromServer = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            sendReceiveSocket.receive(responseFromServer);

            System.out.println("Intermediate Host - Received from Server (String): " + new String(responseFromServer.getData(), 0, responseFromServer.getLength()));
            System.out.println("Intermediate Host - Received from Server (Bytes): " + bytesToHex(Arrays.copyOf(responseFromServer.getData(), responseFromServer.getLength())));

            // Send response from server to client using client's address and port from the received packet
            DatagramPacket sendPacketToClient = new DatagramPacket(responseFromServer.getData(), responseFromServer.getLength(), receivePacket.getAddress(), receivePacket.getPort());
            sendReceiveSocket.send(sendPacketToClient);

            System.out.println("Intermediate Host - Sent back to Client (String): " + new String(sendPacketToClient.getData(), 0, sendPacketToClient.getLength()));
            System.out.println("Intermediate Host - Sent back to Client (Bytes): " + bytesToHex(Arrays.copyOf(sendPacketToClient.getData(), sendPacketToClient.getLength())));
        }
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X ", b));
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        try {
            IntermediateHost intermediateHost = new IntermediateHost();
            intermediateHost.relay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
