package tech.hootlab;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import tech.hootlab.client.ClientSettings;

/*
 * ClientRunner.java
 *
 * Gareth Sears - 2493194S
 *
 * This class manages each client's read and write thread and provides appropriate methods for
 * sending messages to the client as well as filtering the clients' messages and passing them to the
 * controller.
 */
public class ClientRunner {

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

        clientWriter = new ClientWriter();
        writeThread = new Thread(clientWriter);
        writeThread.start();

        readThread = new Thread(new ClientReader());
        readThread.start();

    }

    public String getID() {
        return clientID;
    }

    public void sendMessage(SocketMessage message) {
        clientWriter.sendMessage(message);
    }

    public void disconnect() {
        try {
            // Send a poison pill to the blocking queue to terminate
            sendMessage(new SocketMessage(SocketMessage.POISON, null));
            // Close client connection
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                            throw new IllegalArgumentException("API command not recognised");
                    }
                }

            } catch (EOFException e) {
                controller.removePlayer(clientID);
                // Handles disconnecting both the read and write threads.
                disconnect();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Private class so we can access instance variables
    private class ClientWriter implements Runnable {
        // Uses a blocking queue as may receive messages from other threads sharing the controller.
        private BlockingQueue<SocketMessage> messageQueue = new LinkedBlockingQueue<>();
        private ObjectOutputStream objectOutputStream;

        public ClientWriter() {
            try {
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(SocketMessage message) {
            try {
                messageQueue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean run = true;
            while (run) {
                try {
                    // Takes from the blocking queue. I was originally using a LinkedConcurrentQueue
                    // and checking if it was empty in a loop, but this absolutely killed my CPU.
                    // Lessons learnt...
                    SocketMessage message = messageQueue.take();
                    if (message.getCommand().equals(SocketMessage.POISON)) {
                        // Shut down when poison pill received from client disconnect.
                        run = false;
                    } else {
                        objectOutputStream.writeObject(message);
                        // Reset to avoid caching, as we send the same objects with different
                        // internal states. This bug was a nightmare to find!
                        objectOutputStream.reset();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                objectOutputStream.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return clientID;
    }

}
