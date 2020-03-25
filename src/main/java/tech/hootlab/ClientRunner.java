package tech.hootlab;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import tech.hootlab.client.ClientSettings;

public class ClientRunner implements SocketMessageSender {

    private final Socket client;
    private final String clientID;

    private final ClientWriter clientWriter;

    private final Thread readThread;
    private final Thread writeThread;

    private ServerController controller;

    public ClientRunner(Socket client, ServerController controller) {
        this.controller = controller;
        this.clientID = UUID.randomUUID().toString();
        this.client = client;

        readThread = new Thread(new ClientReader());
        readThread.start();

        clientWriter = new ClientWriter();
        writeThread = new Thread(clientWriter);
        writeThread.start();
    }

    public String getID() {
        return clientID;
    }

    public void sendMessage(SocketMessage message) {
        clientWriter.sendMessage(message);
    }

    public void disconnect() {
        readThread.interrupt();
        writeThread.interrupt();
    }

    // Private class so we can access instance variables
    private class ClientReader implements Runnable {

        ObjectInputStream objectInputStream;

        public ClientReader() {
            try {
                objectInputStream = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                SocketMessage message = null;

                while ((message = (SocketMessage) objectInputStream.readObject()) != null) {
                    switch (message.getCommand()) {

                        case SocketMessage.CONNECT:
                            controller.addPlayer(clientID, (ClientSettings) message.getPayload());
                            break;

                        case SocketMessage.HIT:
                            controller.hit((String) clientID);
                            break;

                        case SocketMessage.STICK:
                            controller.stick((String) clientID);
                            break;

                        case SocketMessage.DEAL:
                            controller.deal((String) clientID);
                            break;

                        default:
                            break;
                    }
                }

                objectInputStream.close();

            } catch (EOFException e) {
                disconnect();
                controller.removePlayer(clientID);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Private class so we can access instance variables
    private class ClientWriter implements Runnable {

        ConcurrentLinkedQueue<SocketMessage> messageQueue = new ConcurrentLinkedQueue<>();
        ObjectOutputStream objectOutputStream;

        public ClientWriter() {
            try {
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(SocketMessage message) {
            messageQueue.add(message);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!messageQueue.isEmpty()) {
                        objectOutputStream.writeObject(messageQueue.poll());
                        // Reset to avoid caching, as we send the same objects with different
                        // internal states. This bug was a nightmare to find!
                        objectOutputStream.reset();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String toString() {
        return clientID;
    }

}
