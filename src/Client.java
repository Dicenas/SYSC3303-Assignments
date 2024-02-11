import java.net.*;
import java.io.*;
import java.util.Arrays;

/**
 * Client class that creates a DatagramSocket to both send and receive requests
 * containing alternating read and write requests as byte arrays.
 * It sends the packet to a well known port: 23 on the intermediate host while
 * the client waits for a response on its DatagramSocket.
 *
 * @author Daniel Godfrey
 */
public class Client {
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

    public Client() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost"); // Use appropriate address
    }

    /**
     * Send alternating read/write requests as packets to port 23.
     *
     * @throws IOException
     */
    public void sendRequest() throws IOException {
        for (int i = 0; i < 11; i++) {
            String requestType = (i % 2 == 0) ? "read" : "write";
            if (i == 10) requestType = "invalid (request #11)"; // For the 11th request

            String fileName = "test" + i + ".txt"; // Sample file name; the file doesn't actually exist
            String mode = (i % 2 == 0) ? "netascii" : "octet"; // Alternate modes
            buf = createRequest(requestType, fileName, mode); // Create request and store in buffer
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 23);

            // Sending request
            System.out.println("Sending request (String): " + new String(buf, 0, buf.length));
            System.out.println("Sending request (Bytes): " + bytesToHex(buf));
            socket.send(packet);

            // Prepare to receive the response
            byte[] responseBuffer = new byte[buf.length]; // Use a new buffer
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Receiving response
            byte[] responseData = Arrays.copyOf(responsePacket.getData(), responsePacket.getLength());
            System.out.println("Received response (String): " + new String(responseData));
            System.out.println("Received response (Bytes): " + bytesToHex(responseData));
        }
    }

    /**
     * Encode the request as a byte array
     *
     * @param requestType read for a read request, or write for a write request
     * @param fileName sample filename string
     * @param mode mode, either netascii or octet
     * @return byte array encoding the request. The first two bits contain the request type,
     *      followed by the filename written in bytes, followed by a 0 byte,
     *      followed by a single byte representing the mode,
     *      and finally a 0 byte indicating the end of the request.
     */
    private byte[] createRequest(String requestType, String fileName, String mode) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        if ("read".equals(requestType)) {
            byteStream.write(0);
            byteStream.write(1); // Second byte is 1 if there is a read request
        } else if ("write".equals(requestType)) {
            byteStream.write(0);
            byteStream.write(2); // Second byte is 2 if there is a write request
        } else {
            throw new IllegalArgumentException("Invalid request type: " + requestType);
        }
        try {
            byteStream.write(fileName.getBytes("UTF-8")); // write fileName in bytes
            byteStream.write(0); // Zero byte between filename and mode
            byteStream.write(mode.toLowerCase().getBytes("UTF-8")); // write mode in bytes
            byteStream.write(0); // Final zero byte
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteStream.toByteArray();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X ", b));
        }
        return stringBuilder.toString();
    }

    public void close() {
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.sendRequest();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
