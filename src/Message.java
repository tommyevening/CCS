import java.net.DatagramPacket;

class Message {
    int length;
    int messageLength;
    String message;
    DatagramPacket packet;

    public Message(int length, int messageLength, String message) {
        this.length = length;
        this.messageLength = messageLength;
        this.message = message;
    }

    public Message(int length, int messageLength, String message, DatagramPacket packet) {
        this(length, messageLength, message);
        this.packet = packet;
    }
}