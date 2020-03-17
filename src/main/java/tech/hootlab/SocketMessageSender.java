package tech.hootlab;

public interface SocketMessageSender {
    String getID();

    void sendMessage(SocketMessage message);
}
