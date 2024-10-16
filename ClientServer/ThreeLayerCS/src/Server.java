import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<Contact> contacts;
    private int nextId;

    public Server() {
        contacts = new ArrayList<>();
        nextId = 1;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("服务器在端口5000上启动");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                while (true) {
                    String command = (String) in.readObject();
                    switch (command) {
                        case "GET_ALL":
                            out.writeObject(new ArrayList<>(contacts));
                            break;
                        case "ADD":
                            Contact newContact = (Contact) in.readObject();
                            newContact.setId(nextId++);
                            contacts.add(newContact);
                            out.writeObject(true);
                            break;
                        case "UPDATE":
                            Contact updatedContact = (Contact) in.readObject();
                            boolean updated = false;
                            for (int i = 0; i < contacts.size(); i++) {
                                if (contacts.get(i).getId() == updatedContact.getId()) {
                                    contacts.set(i, updatedContact);
                                    updated = true;
                                    break;
                                }
                            }
                            out.writeObject(updated);
                            break;
                        case "DELETE":
                            int id = (int) in.readObject();
                            boolean deleted = contacts.removeIf(c -> c.getId() == id);
                            out.writeObject(deleted);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}