import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class GUI extends JFrame {
    private JTextField nameField, addressField, phoneField;
    private JButton addButton, viewButton, editButton, deleteButton;
    private JList<Contact> contactList;
    private DefaultListModel<Contact> listModel;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public GUI() {
        super("个人通讯录");
        initializeGUI();
        connectToServer();
        loadContacts();
    }

    private void initializeGUI() {
        setLayout(new BorderLayout());

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("姓名:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("地址:"));
        addressField = new JTextField(20);
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("电话:"));
        phoneField = new JTextField(20);
        inputPanel.add(phoneField);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加");
        viewButton = new JButton("查看");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // 联系人列表
        listModel = new DefaultListModel<>();
        contactList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(contactList);

        // 将组件添加到框架
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加动作监听器
        addButton.addActionListener(e -> addContact());
        viewButton.addActionListener(e -> viewContact());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "连接服务器失败", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void loadContacts() {
        try {
            out.writeObject("GET_ALL");
            List<Contact> contacts = (List<Contact>) in.readObject();
            listModel.clear();
            for (Contact contact : contacts) {
                listModel.addElement(contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addContact() {
        String name = nameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都是必填的", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            out.writeObject("ADD");
            out.writeObject(new Contact(0, name, address, phone));
            boolean success = (boolean) in.readObject();
            if (success) {
                JOptionPane.showMessageDialog(this, "联系人添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadContacts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "添加联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "添加联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewContact() {
        Contact selected = contactList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请选择一个联系人", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        nameField.setText(selected.getName());
        addressField.setText(selected.getAddress());
        phoneField.setText(selected.getPhone());
    }

    private void editContact() {
        Contact selected = contactList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请选择一个联系人", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = nameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都是必填的", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            out.writeObject("UPDATE");
            out.writeObject(new Contact(selected.getId(), name, address, phone));
            boolean success = (boolean) in.readObject();
            if (success) {
                JOptionPane.showMessageDialog(this, "联系人更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadContacts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "更新联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "更新联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteContact() {
        Contact selected = contactList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请选择一个联系人", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "您确定要删除这个联系人吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.writeObject("DELETE");
                out.writeObject(selected.getId());
                boolean success = (boolean) in.readObject();
                if (success) {
                    JOptionPane.showMessageDialog(this, "联系人删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadContacts();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "删除联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "删除联系人失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        addressField.setText("");
        phoneField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}