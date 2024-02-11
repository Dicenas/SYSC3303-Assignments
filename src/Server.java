import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 * Server class creates a DatagramSocket to use to receive (port 69).
 * It reads, validates, and responds to requests, based on the content of the request.
 *
 * @author Daniel Godfrey
 */
public class Server {
    private DatagramSocket socket;
    private byte[] buf = new byte[256];

    public Server() throws SocketException {
        socket = new DatagramSocket(69);
    }

    /**
     * Receive request relayed from intermediate host.
     * @throws IOException
     */
    public void listen() throws IOException {
        byte[] receiveBuffer = new byte[1024]; // Assuming a buffer size, adjust as necessary

        while (true) {
            // Receive message from intermediate host
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            System.out.println("Server - Received request (String): " + new String(receivePacket.getData(), 0, receivePacket.getLength()));
            System.out.println("Server - Received request (Bytes): " + bytesToHex(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength())));

            // Validate request and prepare response
            byte[] requestData = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
            byte[] response = validateAndRespond(requestData);

            if (response != null) {
                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                DatagramPacket responsePacket = new DatagramPacket(response, response.length, address, port);
                DatagramSocket responseSocket = new DatagramSocket();
                responseSocket.send(responsePacket);
                responseSocket.close(); // close socket after sending response

                System.out.println("Server - Sent response (String): " + new String(response));
                System.out.println("Server - Sent response (Bytes): " + bytesToHex(response));
            } else {
                // Print error message for invalid request
                System.out.println("Server - Invalid request received.");
            }
        }
    }

    /**
     * Validate request relayed from intermediate host, and send response according to request.
     *
     * @param requestData The request as a byte array relayed through the host from the client.
     * @return Byte array 00 03 00 01 if a valid read request is received,
     *      or 00 04 00 00 if a valid write request is received.
     */
    private byte[] validateAndRespond(byte[] requestData) {
        // Basic validation for length and starting bytes
        if (requestData.length < 4 || requestData[0] != 0 || (requestData[1] != 1 && requestData[1] != 2)) {
            System.out.println("Invalid request: Incorrect start or insufficient length.");
            return null;
        }

        // Attempt to find the first zero byte after the opcode, indicating end of filename
        int filenameEndIndex = -1; // Index of the zero byte after the filename
        for (int i = 2; i < requestData.length; i++) {
            if (requestData[i] == 0) {
                filenameEndIndex = i;
                break;
            }
        }

        if (filenameEndIndex == -1) {
            System.out.println("Invalid request: No zero byte after filename.");
            return null;
        }

        // Attempt to find the second zero byte, indicating end of mode
        int modeEndIndex = -1; // Index of the zero byte after the mode
        for (int i = filenameEndIndex + 1; i < requestData.length; i++) {
            if (requestData[i] == 0) {
                modeEndIndex = i;
                break;
            }
        }

        if (modeEndIndex == -1) {
            System.out.println("Invalid request: No zero byte after mode.");
            return null;
        }

        // Check for any extra data after mode, or if buffer length is incorrect
        if (modeEndIndex != requestData.length - 1) {
            System.out.println("Invalid request: Extra data found after mode.");
            return null;
        }

        // Create response based on read (0x01) or write (0x02) request
        byte[] response;
        if (requestData[1] == 1) { // Read request
            response = new byte[] {0, 3, 0, 1};
        } else { // Write request
            response = new byte[] {0, 4, 0, 0};
        }

        System.out.println("Request is valid. Sending response.");
        return response;
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
            Server server = new Server();
            server.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
