package client;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import users.Customer;
import users.Message;
import users.NotSCException;
import users.Roles;
import users.Seller;
import users.Store;
import users.User;

public class CLIClient extends JComponent implements Runnable {
    User sessionUser;
    String dest;
    ArrayList<Message> messageList;
    int oldX; // previous mouse x coordinate
    int oldY; // previous mouse y coordinate
    CLIClient client;
    //components of the Panel
    //login/register functionality
    JTextField usernameTextField;
    JTextField passwordTextField;
    JButton loginButton;
    JButton registerButton;
    JButton logoutButton;

    //all user options
    static JTextArea instructionsTextArea;
    JTextArea contentTextArea;
    static JTextField userInputTextField;
    JButton blockUserButton;

    static JButton accountManagementButton;
    JButton deleteAccountButton;
    JButton modifyAccountButton;

    static JButton messageButton;
    JButton sendMessageButton;
    JButton loadConversationButton;
    JButton editMessageButton;
    JButton deleteMessageButton;

    //seller specific options
    static JButton addStoreButton;
    static JButton viewStoresButton;

    //cutomer specific options
    JButton messageStoreButton;
    
    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginButton) { //WORKING
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();
                sessionUser = client.loginButton(username, password);
                if (sessionUser != null) {
                    contentTextArea.setText("Successfully logged in as " + sessionUser.getEmail().toString());
                    loginButton.setVisible(false);
                    registerButton.setVisible(false);
                    if (sessionUser.getUserType().toString().equals("Customer")) {
                        instructionsTextArea.setText("Click 'Account Management' to manage your account or click 'Message' to send messages or view your existing conversations.");
                    } else {
                        instructionsTextArea.setText("Click 'Account Management' to manage your account, click 'Message' to send messages or view your existing conversations, or enter your store's name in the user input input field and click 'Add Store' to add a new store.");
                    }
                } else {
                    contentTextArea.setText("Couldn't log in. Try again");
                }
            }
            if (e.getSource() == registerButton) { //WORKING
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();
                String role = userInputTextField.getText();
                String message = client.registerButton(username, password, role);
                contentTextArea.setText(message);
            }
            if (e.getSource() == logoutButton) { //WORKING BUT NEEDS TO MAKE OTHER BUTTONS INVISIBLE LATER
                sessionUser = null; //reset sessionUser

                usernameTextField.setText("username");
                passwordTextField.setText("password");
                instructionsTextArea.setText("Login by entering your username and password. If you're registering a user, register by entering your username and password and designate your role by typing 'Seller' or 'Customer' into the user input field");
                contentTextArea.setText("Successfully logged out!");
                userInputTextField.setText("user input");

                addStoreButton.setVisible(false);
                accountManagementButton.setVisible(false);
                messageButton.setVisible(false);
                deleteAccountButton.setVisible(false);
                modifyAccountButton.setVisible(false);
                viewStoresButton.setVisible(false);
                sendMessageButton.setVisible(false);
                editMessageButton.setVisible(false);
                deleteMessageButton.setVisible(false);
                messageStoreButton.setVisible(false);
                loadConversationButton.setVisible(false);
                blockUserButton.setVisible(false);

                loginButton.setVisible(true);
                registerButton.setVisible(true);
            }
            if (e.getSource() == accountManagementButton) { //WORKING

                modifyAccountButton.setVisible(true);
                deleteAccountButton.setVisible(true);
                addStoreButton.setVisible(true);

                loadConversationButton.setVisible(false);
                sendMessageButton.setVisible(false);
                editMessageButton.setVisible(false);
                deleteMessageButton.setVisible(false);
                blockUserButton.setVisible(false);
                
                String instructions = "Delete this account with the 'Delete Account' button, or modify the account's username, password or both by entering your new username and/or password in the username and password textboxes";
                if (sessionUser.getUserType() == Roles.Customer) {
                    instructionsTextArea.setText(instructions);
                } else {
                    instructionsTextArea.setText(instructions + ", or add a new store by typing the store's name in the user input text box");
                }
            }
            if (e.getSource() == messageButton) { //WORKS
                dest = null; // reset message destination

                contentTextArea.setText(client.messageButton());
                sendMessageButton.setVisible(true);
                blockUserButton.setVisible(true);
                loadConversationButton.setVisible(true);

                if(sessionUser.getUserType() == Roles.Customer) {
                    messageStoreButton.setVisible(true);
                    instructionsTextArea.setText("Select an existing conversation by typing the email of the user in the user input input field, or type in a user from the list along with the message you want to send seperated by a comma (,) to create a new conversation, or type the store's name along with the message you want to send seperated by a comma. The format for sending a message is [email],[message] or [store],[message]");
                } else {
                    instructionsTextArea.setText("Select an existing conversation by typing the email of the user in the user input input field or type in a user from the list along with the message you want to send seperated by a comma (,) to create a new conversation. The format for sending a message is [email],[message]");
                }

                editMessageButton.setVisible(false);
                deleteMessageButton.setVisible(false);
                deleteAccountButton.setVisible(false);
                modifyAccountButton.setVisible(false);
                addStoreButton.setVisible(false);

                
            }
            if (e.getSource() == blockUserButton) {
                String blockUser = userInputTextField.getText();
                if (usermap.values().contains(new Seller(blockUser))) {
                    sessionUser.blockUsers(new Seller(blockUser));
                    contentTextArea.setText(blockUser + " has been blocked");
                } else if (usermap.values().contains(new Customer(blockUser))) {
                    sessionUser.blockUsers(new Seller(blockUser));
                    contentTextArea.setText(blockUser + " has been blocked");
                } else {
                    contentTextArea.setText("Can't block user. No user found");
                }
            }
            if (e.getSource() == addStoreButton) { //WORKS
                deleteAccountButton.setVisible(false);
                modifyAccountButton.setVisible(false);
                loadConversationButton.setVisible(false);
                sendMessageButton.setVisible(false);
                editMessageButton.setVisible(false);
                deleteMessageButton.setVisible(false);

                if (!userInputTextField.getText().isBlank()) {
                    new Store(new Seller(sessionUser.getEmail()), userInputTextField.getText());
                    contentTextArea.setText("You've added a new store with the name " + userInputTextField.getText());
                } else {
                    contentTextArea.setText("Please enter your store's name before attempting to create a new store");
                }
            }
            if (e.getSource() == viewStoresButton) {
                String owner = sessionUser.getEmail();
                Map<String, String> storeList = Store.getStores();
                String storesList = "Your stores:\n";
                for (Map.Entry<String, String> entry : storeList.entrySet()) {
                    if (entry.getValue().equals(owner)) {
                        storesList += entry.getKey() + "\n";
                    }
                }
                contentTextArea.setText(storesList);

                loadConversationButton.setVisible(false);
                sendMessageButton.setVisible(false);
                blockUserButton.setVisible(false);
                addStoreButton.setVisible(false);
                deleteAccountButton.setVisible(false);
                modifyAccountButton.setVisible(false);
            }
            if (e.getSource() == messageStoreButton) {
                Map<String, String> storeList = Store.getStores();
                String[] content = userInputTextField.getText().split(",");
            
                if (content.length == 2) {
                    String storeName = content[0];
                    String message = content[1];
            
                    if (storeList.containsKey(storeName)) {
                        String storeOwner = storeList.get(storeName);
                        User destUser = null;

                        if (sessionUser.getUserType() == Roles.Customer) {
                            if (!usermap.values().contains(destUser = new Seller(storeOwner)) && !destUser.getBlockedUsers().contains(sessionUser)) { //checks for if the session user is blocked
                                try {
                                    sendMessage(message, sessionUser, destUser);
                                    contentTextArea.setText("Successfully sent the owner of " + storeName + " a message!");
                                } catch (NotSCException ex) {
                                    contentTextArea.setText("Failed to send a message to the owner of " + storeName + "! Either the store doesn't exist or the owner has blocked you.");
                                }
                            }
                        }
                    }
                } else {
                    contentTextArea.setText("Can't send the message to the store! Make sure you're using the correct store name and correct formatting");
                }
            }
            if (e.getSource() == deleteAccountButton) { //WORKS BUT DOESN'T IMMEDIATELY SHOW THE ACCOUNT IS DELETED (SHOWS ON NEXT RUN OF APPLIATION)
                int success = new Login(ACCOUNT_INFO_PATH).deleteAccount(sessionUser.getEmail());
                if (success == 1) {
                    contentTextArea.setText("Email not found!");
                } else {
                    contentTextArea.setText("Account deleted successfully! You've been logged out.");
                }

                //Logout user after deleting account
                usernameTextField.setText("username");
                passwordTextField.setText("password");
                instructionsTextArea.setText("Login by entering your username and password. If you're registering a user, register by entering your username and password and designate your role by typing 'Seller' or 'Customer' into the user input field");
                userInputTextField.setText("user input");

                addStoreButton.setVisible(false);
                accountManagementButton.setVisible(false);
                messageButton.setVisible(false);
                deleteAccountButton.setVisible(false);
                modifyAccountButton.setVisible(false);
                viewStoresButton.setVisible(false);
            }
            if (e.getSource() == modifyAccountButton) { //WORKS BUT DOES'NT MODIFY UNTIL PROGRAM IS RESET/RERUN
                String newEmail = usernameTextField.getText();
                String newPassword = passwordTextField.getText();
                int success = client.modifyAccountButton(newEmail, newPassword);
            
                if (success == 1) {
                    contentTextArea.setText("Email not found!");
                } else if (success == 2) {
                    contentTextArea.setText("Invalid email format!");
                } else if (success == 3) {
                    contentTextArea.setText("Email already exists! Please choose a different one.");
                } else if (success == 4) {
                    contentTextArea.setText("Account updated successfully!");
                } else {
                    contentTextArea.setText("There was an error updating your account.");
                }
            }
            if (e.getSource() == editMessageButton) {
                String[] inputContent = userInputTextField.getText().split(",");
                if (inputContent.length != 2) {
                    contentTextArea.append("\nCouldn't edit the message! Please fomat your input correctly to edit a message.");
                } else {
                    int index = Integer.parseInt(inputContent[0]);
                    String message = inputContent[1];

                    client.editMessageButton(index, message);
                    contentTextArea.setText("Message edited!");
                }
            }
            if (e.getSource() == deleteMessageButton) {
                int index = 0;
                try {
                    index = Integer.parseInt(userInputTextField.getText());
                } catch (Exception ex) {
                    contentTextArea.append("\nPlease enter an integer value!");
                }
                
                client.deleteMessageButton(index);
                contentTextArea.setText("Message deleted!");
            }
            if (e.getSource() == sendMessageButton) { //WORKS
                String message = "";
                int output = 0;
                if (dest != null) {
                    message = userInputTextField.getText();
                } else {
                    String[] input = userInputTextField.getText().split(",");
                    if (input.length != 2) {
                        contentTextArea.setText("Couldn't send a message! Make sure you're formatting your message correctly!");
                    } else {
                        dest = input[0];
                        message = input[1];
                    }
                }

                try {
                    output = client.sendMessageButton(message, dest);
                } catch (NotSCException ex) {
                    ex.printStackTrace();
                }

                if (output == 1) {
                    contentTextArea.setText("You've been blocked by the user.");
                } else if (output == 2) {
                    contentTextArea.setText("Successfully sent the message!");
                } else {
                    contentTextArea.setText("Error");
                }
            }
            
            if (e.getSource() == loadConversationButton) { //WORKS BUT WHEN REENTERING IT SHOWS THE PREVIOUS VERSION OF THE CONVERSATION AS WELL                
                sendMessageButton.setVisible(true);
                editMessageButton.setVisible(true);
                deleteMessageButton.setVisible(true);
                
                loadConversationButton.setVisible(false);
                blockUserButton.setVisible(false);
                
                dest = userInputTextField.getText();
                User destUser = null;

                User[] receivers = sessionUser.getReceiver().toArray(User[]::new);

                for (int i = 0; i < receivers.length; i++) {
                    if (receivers[i].toString().equals(dest)) {
                        destUser = receivers[i];
                    }
                }
                if (destUser != null) {
                    String messages = client.loadConversationButton(destUser);
                    contentTextArea.setText(messages);
                    instructionsTextArea.setText("Send a message by typing the message you want to send, or edit a message by typing the message number and the edited message seperated by a comma ([message index],[edited message]), or delete a message by typing the message's number");
                } else {
                    contentTextArea.append("\n Failed to load the conversation. Please enter a valid email!");
                }
            }
        }
    };

    public User loginButton(String username, String password) {
        String uid = login.loginUser(username, password);
        if (uid != null) {
            if (usermap.get(uid).getUserType().equals(Roles.Customer)) {
                sessionUser = new Customer(uid);
            } else {
                sessionUser = new Seller(uid);
            }
            operations(sessionUser);
        } else {
            return null;
        }
        return sessionUser;
    }


    public String registerButton(String username, String password, String role) {
        int success = login.registerUser(username, password, role);
        String message = "";
        if(success == 1) {
            message = "Username already exists. Please choose a different one.";
        } else if (success == 2) {
            message = "Invalid email format!";
        } else if (success == 3) {
            message = "Registration successful!";
        } else if (success == 4) {
            message = "Error writing to the file";
        } else if (success == 5) {
            message = "Role can only be Seller and Customer!";
        }
        login.saveUserAccountsToFile();
        return message;
    }

    public int modifyAccountButton(String newEmail, String newPassword) {
        int success = 0;

        success = new Login(ACCOUNT_INFO_PATH).editAccount(sessionUser, sessionUser.getEmail(), sessionUser.getPassword(), newEmail, newPassword);
        return success;
    }
    public String messageButton() {
        sessionUser.loadMessage();
        User[] receivers = sessionUser.getReceiver().toArray(User[]::new);
        
        String stores = "All stores include:\n";
        if (sessionUser.getUserType() == Roles.Customer) {
            Map<String, String> storeList = Store.getStores();
                for (String key : storeList.keySet()) {
                    stores += key + "\n";
                }
            }

        String existingConversations = "Existing conversations:\n";
        for (User auser : receivers) {
            existingConversations += (auser + "\n");
        }

        String allRecievers = "";
        ArrayList<User> blockedUsers = sessionUser.getBlockedUsers();
        if (sessionUser.getUserType() == Roles.Seller) {
            StringBuilder sb = new StringBuilder(allRecievers);
            usermap.values().stream()
                .filter(user -> user.getUserType() == Roles.Customer && blockedUsers.stream().noneMatch(blockedUser -> blockedUser.getEmail().equals(user.getEmail())))
                .forEach(user -> sb.append(user.getEmail()).append("\n"));
            allRecievers = sb.toString();

            String finalMessage = existingConversations + "\nList of users you can message:\n" + allRecievers;
            return finalMessage;
        } else if (sessionUser.getUserType() == Roles.Customer) {
            StringBuilder sb = new StringBuilder(allRecievers);
            usermap.values().stream()
                .filter(user -> user.getUserType() == Roles.Seller && blockedUsers.stream().noneMatch(blockedUser -> blockedUser.getEmail().equals(user.getEmail())))
                .forEach(user -> {
                    sb.append(user.getEmail());
                    sb.append("\n");
                });
            allRecievers = sb.toString();
        }
        String finalMessage = stores + existingConversations + "\nList of users you can message:\n" + allRecievers;
        return finalMessage;
    }

    public int sendMessageButton(String mess, String dest) throws NotSCException {
        String message = mess;
        User sender = sessionUser;
        User destUser = null;
        //Checking customer list
        if (sessionUser.getUserType().toString().equals("Customer")) {
            for (int i = 0; i < usermap.size(); i++) {
                if (!usermap.values().contains(destUser = new Seller(dest))) {
                    return 5; //user doesn't exist error
                }
            }
        } else {
            for (int i = 0; i < usermap.size(); i++) {
                if (!usermap.values().contains(destUser = new Customer(dest))) {
                    return 5; //user doesn't exist error
                }
            }
        }
        
        if (checkAccount(dest)) {
            try {
                return sendMessage(message, sender, destUser);
            } catch (NotSCException e) {
                return 6;
            }
        } else {
            return 7;
        }
    }

    public String loadConversationButton(User user) {
        String messages = "";
        int index = 0;
        messageList = sessionUser.getCon(user);
        for (int i = 0; i < messageList.size(); i++) {
            messages += index + ". " + messageList.get(i).toString() + "\n";
            index += 1;
        }
        return messages;
    }

    public void editMessageButton(int index, String message) {
        messageList.get(index).setMessage(message);
        messageList.get(index).writeToRecord();
        Message.tidy();
    }

    public void deleteMessageButton(int index) {
        if (sessionUser.getUserType().equals(Roles.Seller)) {
            messageList.get(index).setSellerVis(false);
        } else {
            messageList.get(index).setCustomerVis(false);
        }
        messageList.get(index).writeToRecord();
        Message.tidy();
    }

    private static final String ACCOUNT_INFO_PATH = "file" + File.separator + "account_list.txt";

    private static Login login = new Login(ACCOUNT_INFO_PATH);
    private static Map<String, User> usermap = login.getUsers();

    public void run() {
        JFrame frame = new JFrame();
        frame.setTitle("Project 5");
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        client = new CLIClient(); //initializing client

        frame.setSize(800, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //create login/logout/register items
        usernameTextField = new JTextField(15);
        passwordTextField = new JTextField(15);

        loginButton = new JButton("Login");
        loginButton.addActionListener(actionListener);

        registerButton = new JButton("Register");
        registerButton.addActionListener(actionListener);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(actionListener);

        //create user items
        instructionsTextArea = new JTextArea();
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setWrapStyleWord(true);
        instructionsTextArea.setPreferredSize(new Dimension(235, 200));

        contentTextArea = new JTextArea();
        contentTextArea.setLineWrap(true);
        contentTextArea.setWrapStyleWord(true);
        contentTextArea.setPreferredSize(new Dimension(235, 200));
        userInputTextField = new JTextField(70);

        accountManagementButton = new JButton("Account Management");
        accountManagementButton.setVisible(false);
        accountManagementButton.addActionListener(actionListener);

        messageButton = new JButton("Message");
        messageButton.setVisible(false);
        messageButton.addActionListener(actionListener);

        blockUserButton = new JButton("Block User");
        blockUserButton.setVisible(false);
        blockUserButton.addActionListener(actionListener);
        
        //create seller specific items
        addStoreButton = new JButton("Add Store");
        addStoreButton.setVisible(false);
        addStoreButton.addActionListener(actionListener);

        viewStoresButton = new JButton("View Stores");
        viewStoresButton.setVisible(false);
        viewStoresButton.addActionListener(actionListener);

        //create customer specific item
        messageStoreButton = new JButton("Message Store");
        messageStoreButton.setVisible(false);
        messageStoreButton.addActionListener(actionListener);

        //Instantiate text fields
        usernameTextField.setText("username");
        passwordTextField.setText("password");
        instructionsTextArea.setText("Login by entering your username and password. If you're registering a user, register by entering your username and password and designate your role by typing 'Seller' or 'Customer' into the user input field");
        contentTextArea.setText("");
        userInputTextField.setText("user input");

        //Adding items to JPanel
        //Login/Logout/Register
        JPanel topPanel = new JPanel();
        topPanel.add(usernameTextField);
        topPanel.add(passwordTextField);
        topPanel.add(loginButton);
        topPanel.add(registerButton);
        topPanel.add(logoutButton);
        content.add(topPanel, BorderLayout.NORTH);

        //User & Seller Items
        Box leftPanel = Box.createVerticalBox();
        leftPanel.add(accountManagementButton);
        leftPanel.add(messageButton);
        leftPanel.add(viewStoresButton);
        content.add(leftPanel, BorderLayout.WEST);

        //Instructions and Message content
        JPanel centerPanel = new JPanel();
        centerPanel.add(instructionsTextArea);
        centerPanel.add(contentTextArea);
        content.add(centerPanel, BorderLayout.CENTER);

        //User input text field
        JPanel bottomPanel = new JPanel();
        
        bottomPanel.add(userInputTextField);
        content.add(bottomPanel, BorderLayout.SOUTH);

        //Account management buttons and sendmessage buttons
        deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.addActionListener(actionListener);
        deleteAccountButton.setVisible(false);

        modifyAccountButton = new JButton("Modify Account");
        modifyAccountButton.addActionListener(actionListener);
        modifyAccountButton.setVisible(false);

        sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(actionListener);
        sendMessageButton.setVisible(false);

        loadConversationButton = new JButton("Load Conversation");
        loadConversationButton.addActionListener(actionListener);
        loadConversationButton.setVisible(false);

        editMessageButton = new JButton("Edit Message");
        editMessageButton.addActionListener(actionListener);
        editMessageButton.setVisible(false);

        deleteMessageButton = new JButton("Delete Message");
        deleteMessageButton.addActionListener(actionListener);
        deleteMessageButton.setVisible(false);

        Box rightPanel = Box.createVerticalBox();
        rightPanel.add(deleteAccountButton);
        rightPanel.add(modifyAccountButton);
        rightPanel.add(loadConversationButton);
        rightPanel.add(sendMessageButton);
        rightPanel.add(editMessageButton);
        rightPanel.add(deleteMessageButton);
        rightPanel.add(blockUserButton);
        rightPanel.add(addStoreButton);
        rightPanel.add(messageStoreButton);
        content.add(rightPanel, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new CLIClient());
    }

    public CLIClient() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /* set oldX and oldY coordinates to beginning mouse press*/
                oldX = e.getX();
                oldY = e.getY();
            }
        });
    }

    private static boolean operations(User user) {
        if (user.getUserType().equals(Roles.Seller)) {
            //set seller specific buttons visible
            viewStoresButton.setVisible(true);
        }
        //make all operations buttons visible along with instructions
        accountManagementButton.setVisible(true);
        messageButton.setVisible(true);

        return true;
    }

    private static boolean checkAccount(String address) {
        return usermap.containsKey(address);
    }

    private static int sendMessage(String message, User sender, User dest) throws NotSCException {
        Seller seller = null;
        Customer customer = null;
        //System.out.println("Please input the message.");
        if (sender.getUserType().equals(dest.getUserType())) {
            throw new NotSCException(
                    "Error: Do not send to " + sender.getUserType().name() + " as a " + sender.getUserType().name());
        }
        if (dest.getBlockedUsers().contains(sender)) {
            //System.out.println("You've been blocked by the user.");
            return 1;
        }
        if (sender.getUserType().equals(Roles.Seller)) {
            seller = new Seller(sender.getEmail());
            customer = new Customer(dest.getEmail());
        } else {
            seller = new Seller(dest.getEmail());
            customer = new Customer(sender.getEmail());
        }

        new Message(seller, customer, message, true, true, sender.getUserType().equals(Roles.Seller))
                .writeToRecord();

        Message.tidy();

        return 2;
    }
}
