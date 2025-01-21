import java.net.DatagramSocket;
import java.net.SocketException;

class UDPDiscoveryServer implements Runnable {
    private DatagramSocket udpSocket;
    private int port;
    private volatile boolean running = true;

    public UDPDiscoveryServer(int port) throws SocketException {
        this.port = port;
        this.udpSocket = new DatagramSocket(port); // Otwiera gniazdo UDP
    }

    public void run() {
        // Nasłuchuje na wiadomości UDP
        try {
            UDPHandler udpHandler = new UDPHandler(udpSocket);
            while (running) {
                Message receivedMessage = udpHandler.getMessage(); // Odbiera wiadomość
                if (receivedMessage.message.startsWith("CCS DISCOVER")) {
                    // Odpowiada na zapytanie o usługę
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
        // Zatrzymuje serwer UDP
        running = false;
        if (udpSocket != null) {
            udpSocket.close();
        }
    }
}