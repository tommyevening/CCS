
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPHandler {
    private DatagramSocket socket;

    public UDPHandler(DatagramSocket socket) {
        this.socket = socket;
    }

    public Message getMessage() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        return new Message(packet.getLength(), packet.getLength(), receivedMessage, packet);
    }

    public void sendMessage(InetAddress address, int port, Message message) throws IOException {
        byte[] buffer = message.message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }
}