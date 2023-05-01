import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Login {
    private Map<String, User> users;
    private static final String SERVER = "127.0.0.1";
    private static final int PORT = 8888;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public Login() {
        users = new HashMap<>();
        loadUsernamesAndPasswords();
    }

    private boolean loadUsernamesAndPasswords() {
        try (Socket socket = new Socket(SERVER, PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            sendHeader(pw, 0);
            reader.lines().filter(s -> !s.isBlank()).forEach(l -> {
                String[] sigs = l.split(";");
                users.put(sigs[0], new User(sigs[0], sigs[1], Roles.valueOf(sigs[2])));
            });

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
            }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public String loginUser(String username, String pass) {
        String emailAdd = username;

        if (!users.containsKey(emailAdd)) {
            // System.err.println("Usernpw.write("account\n0\n");ame not found.");
            return null;
        }

        String password = pass;

        if (!password.equals(users.get(emailAdd).getPassword())) {
            // System.err.println("Incorrect password.");
            return null;
        }

        // System.out.println("Login successful!");

        return users.get(emailAdd).getEmail();
    }

    public int registerUser(String username, String password, String role) {
        String newUsername = username;

        if (users.containsKey(newUsername)) {
            // System.out.println("Username already exists. Please choose a different
            // one.");
            return 1;
        }

        if (!EMAIL_PATTERN.matcher(newUsername).matches()) {
            // System.out.println("Invalid email format!");
            return 2;
        }

        String newPassword = password;
        String newRole = role;

        users.put(newUsername, new User(newUsername, newPassword, Roles.valueOf(newRole)));
        saveUserAccountsToFile();
        return 3;

    }

    public int deleteAccount(String email) {
        if (!users.containsKey(email)) {
            // System.out.println("Email not found!");
            return 1;
        }

        users.remove(email);
        saveUserAccountsToFile();
        // System.out.println("Account deleted successfully!");
        return 2;
    }

    public int editAccount(User cUser, String currentEmail, String currentPassword, String newEmailAdd,
            String newPass) {
        if (!users.containsKey(currentEmail)) {
            // System.out.println("Email not found!");
            return 1;
        }
        User currentUser = cUser; // NEEDS EDIT
        if (newEmailAdd != null) {
            if (!newEmailAdd.isEmpty()) {
                if (!EMAIL_PATTERN.matcher(newEmailAdd).matches()) {
                    // System.out.println("Invalid email format!");
                    return 2;
                }
                if (users.containsKey(newEmailAdd)) {
                    // System.out.println("Email already exists! Please choose a different one.");
                    return 3;
                }
                users.remove(currentEmail);
                currentUser.setEmail(newEmailAdd);
                currentEmail = newEmailAdd;
            }
        }
        if (newPass != null) {
            if (!newPass.isEmpty()) {
                currentUser.setPassword(newPass);
            }
        }
        users.put(currentEmail, currentUser);
        saveUserAccountsToFile();
        // System.out.println("Account updated successfully!");
        return 4;
    }

    public void saveUserAccountsToFile() {
        try (Socket socket = new Socket(SERVER, PORT);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                    sendHeader(writer, 2);
            writer.println(users.values().size());
            writer.flush();
            for (User user : users.values()) {
                writer.write(user.getEmail() + ";" + user.getPassword() + ";" + user.getUserType().name() + ";" + String
                        .join(",", user.getBlockedUsers().stream().toArray(String[]::new))
                        + "\n");
                writer.flush();
            }for (User user : users.values()) {
                String blocked = "";
                String notSeen = "";
                for (int i = 0; i < user.getBlockedUsers().size(); i++) {
                    blocked += user.getBlockedUsers().get(i);
                    if (i < user.getBlockedUsers().size() - 1)
                        blocked += ",";
                }
                for (int i = 0; i < user.getNoSeeList().size(); i++) {
                    notSeen += user.getNoSeeList().get(i);
                    if (i < user.getBlockedUsers().size() - 1)
                        notSeen += ",";
                }
                writer.write(user.getEmail() + ";" + user.getPassword() + ";" + user.getUserType().name() + ";"
                        + blocked + ";"
                        + notSeen + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to a file: " + e.getMessage(), "Error!",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendHeader(PrintWriter pw, int options) {
        pw.printf("account\n%d\n", options);
        pw.flush();
    }
}
