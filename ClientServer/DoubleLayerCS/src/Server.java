import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private List<Contact> contacts;
    private int nextId;

    public Server() {
        contacts = new ArrayList<>();
        nextId = 1;
    }

    public String handleRequest(String request) {
        String[] parts = request.split("\\|");
        String action = parts[0];
        String response = "";

        switch (action) {
            case "view":
                response = viewContacts();
                break;
            case "add":
                response = addContact(parts);
                break;
            case "edit":
                response = editContact(parts);
                break;
            case "delete":
                response = deleteContact(parts);
                break;
        }

        return response;
    }

    private String viewContacts() {
        StringBuilder sb = new StringBuilder("成功|");
        for (Contact contact : contacts) {
            sb.append(contact.toString()).append(",");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private String addContact(String[] parts) {
        Contact newContact = new Contact(
                nextId++,
                parts[1],
                parts[2],
                parts[3]
        );
        contacts.add(newContact);
        return "成功|联系人添加成功";
    }

    private String editContact(String[] parts) {
        int id = Integer.parseInt(parts[1]);
        for (Contact contact : contacts) {
            if (contact.getId() == id) {
                contact.setName(parts[2]);
                contact.setAddress(parts[3]);
                contact.setPhone(parts[4]);
                return "成功|联系人更新成功";
            }
        }
        return "错误|未找到联系人";
    }

    private String deleteContact(String[] parts) {
        int id = Integer.parseInt(parts[1]);
        for (Iterator<Contact> iterator = contacts.iterator(); iterator.hasNext();) {
            Contact contact = iterator.next();
            if (contact.getId() == id) {
                iterator.remove();
                return "成功|联系人删除成功";
            }
        }
        return "错误|未找到联系人";
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("服务器正在监听5000端口");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine = in.readLine();
                String response = handleRequest(inputLine);
                out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Contact {
        private int id;
        private String name;
        private String address;
        private String phone;

        public Contact(int id, String name, String address, String phone) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.phone = phone;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        @Override
        public String toString() {
            return id + ":" + name + ":" + address + ":" + phone;
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}