import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    private String sendRequest(String request) throws IOException {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();
        }
    }

    private void viewContacts() throws IOException {
        String response = sendRequest("view");
        String[] parts = response.split("\\|");
        if (parts[0].equals("成功")) {
            if (parts.length > 1) {
                String[] contacts = parts[1].split(",");
                for (String contact : contacts) {
                    String[] contactInfo = contact.split(":");
                    System.out.println("ID: " + contactInfo[0] +
                            ", 姓名: " + contactInfo[1] +
                            ", 地址: " + contactInfo[2] +
                            ", 电话: " + contactInfo[3]);
                }
            } else {
                System.out.println("没有联系人");
            }
        } else {
            System.out.println("获取联系人失败");
        }
    }

    private void addContact() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入姓名: ");
        String name = scanner.nextLine();
        System.out.print("输入地址: ");
        String address = scanner.nextLine();
        System.out.print("输入电话: ");
        String phone = scanner.nextLine();

        String request = String.format("add|%s|%s|%s", name, address, phone);
        String response = sendRequest(request);
        System.out.println(response.split("\\|")[1]);
    }

    private void editContact() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入要编辑的联系人ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符

        System.out.print("输入新姓名: ");
        String name = scanner.nextLine();
        System.out.print("输入新地址: ");
        String address = scanner.nextLine();
        System.out.print("输入新电话: ");
        String phone = scanner.nextLine();

        String request = String.format("edit|%d|%s|%s|%s", id, name, address, phone);
        String response = sendRequest(request);
        System.out.println(response.split("\\|")[1]);
    }

    private void deleteContact() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入要删除的联系人ID: ");
        int id = scanner.nextInt();

        String request = String.format("delete|%d", id);
        String response = sendRequest(request);
        System.out.println(response.split("\\|")[1]);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n个人通讯录系统");
            System.out.println("1. 查看联系人信息");
            System.out.println("2. 添加新联系人");
            System.out.println("3. 修改联系人信息");
            System.out.println("4. 删除联系人");
            System.out.println("5. 退出");
            System.out.print("请选择操作: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符

            try {
                switch (choice) {
                    case 1:
                        viewContacts();
                        break;
                    case 2:
                        addContact();
                        break;
                    case 3:
                        editContact();
                        break;
                    case 4:
                        deleteContact();
                        break;
                    case 5:
                        System.out.println("谢谢使用，再见！");
                        return;
                    default:
                        System.out.println("无效的选择，请重试。");
                }
            } catch (Exception e) {
                System.out.println("操作失败: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}