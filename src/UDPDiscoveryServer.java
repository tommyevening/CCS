import java.net.DatagramSocket;
import java.net.SocketException;

class UDPDiscoveryServer implements Runnable {
    private DatagramSocket udpSocket;
    private int port;
    private volatile boolean running = true;

    public UDPDiscoveryServer(int port) throws SocketException {
        this.port = port;
        this.udpSocket = new DatagramSocket(port);
    }

    public void run() {
        try {
            UDPHandler udpHandler = new UDPHandler(udpSocket);
            while (running) {
                Message receivedMessage = udpHandler.getMessage();
                if (receivedMessage.message.startsWith("CCS DISCOVER")) {
                    Message responseMessage = new Message(0, "CCS FOUND".length(), "CCS FOUND");
                    udpHandler.sendMessage(receivedMessage.packet.getAddress(), receivedMessage.packet.getPort(), responseMessage);
                }
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        running = false;
        if (udpSocket != null) {
            udpSocket.close();
        }
    }
}