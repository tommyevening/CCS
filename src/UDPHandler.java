import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPHandler {
    private DatagramSocket socket;

    public UDPHandler(DatagramSocket socket) {
        this.socket = socket; // Inicjalizuje gniazdo UDP
    }

    public Message getMessage() throws IOException {
        // Odbiera wiadomość UDP
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        return new Message(packet.getLength(), packet.getLength(), receivedMessage, packet); // Zwraca odebraną wiadomość
    }

    public void sendMessage(InetAddress address, int port, Message message) throws IOException {
        // Wysyła wiadomość UDP do określonego adresu i portu
        byte[] buffer = message.message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }
}