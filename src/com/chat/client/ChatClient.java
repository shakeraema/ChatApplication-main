package com.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 60000;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JFrame frame = new JFrame("Chat Client");
    private JTextArea messageArea = new JTextArea(20, 50);
    private JTextField inputField = new JTextField(40);
    private JTextField groupField = new JTextField(20);
    private JButton sendButton = new JButton("Send");
    private JButton createJoinButton = new JButton("Create/Join Group");
    private JButton emojiButton = new JButton("Send Emoji");
    private JButton listMembersButton = new JButton("List Members");
    private JButton attachFileButton = new JButton("Attach File");

    public ChatClient() {
        // Layout GUI
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Group:"));
        topPanel.add(groupField);
        topPanel.add(createJoinButton);
        topPanel.add(listMembersButton);

        frame.add(topPanel, BorderLayout.NORTH);

        messageArea.setEditable(false);
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(inputField);
        bottomPanel.add(sendButton);
        bottomPanel.add(emojiButton);
        bottomPanel.add(attachFileButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);

        // Action listeners
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(inputField.getText());
                inputField.setText("");
            }
        });

        createJoinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage("/join " + groupField.getText());
            }
        });

        listMembersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage("/members");
            }
        });

        emojiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage("\uD83D\uDE00"); // Unicode for 😀 emoji
            }
        });

        attachFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    sendFile(selectedFile);
                }
            }
        });

    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void sendFile(File file) {
    try {
        out.println("/file " + file.getName());
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        socket.getOutputStream().write(fileBytes);
        socket.getOutputStream().flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                messageArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }
}
