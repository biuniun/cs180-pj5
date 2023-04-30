package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import users.Roles;
import users.User;

public class Login {
    private Map<String, User> users;
    private String filePath;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public Login(String filePath) {
        this.filePath = filePath;
        users = new HashMap<>();
        loadUsernamesAndPasswords();
    }

    private boolean loadUsernamesAndPasswords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.lines().filter(s -> !s.isBlank()).forEach(l -> {
                String[] sigs = l.split(";");
                users.put(sigs[0], new User(sigs[0], sigs[1], Roles.valueOf(sigs[2])));
            });

            return true;
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false;
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public String loginUser(String username, String pass) {
        String emailAdd = username;

        if (!users.containsKey(emailAdd)) {
            //System.err.println("Username not found.");
            return null;
        }

        String password = pass;

        if (!password.equals(users.get(emailAdd).getPassword())) {
            //System.err.println("Incorrect password.");
            return null;
        }

        //System.out.println("Login successful!");

        return users.get(emailAdd).getEmail();
    }

    public int registerUser(String username, String password, String role) {
        String newUsername = username;

        if (users.containsKey(newUsername)) {
            //System.out.println("Username already exists. Please choose a different one.");
            return 1;
        }

        if (!EMAIL_PATTERN.matcher(newUsername).matches()) {
            //System.out.println("Invalid email format!");
            return 2;
        }

        String newPassword = password;
        String newRole = role;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            users.put(newUsername, new User(newUsername, newPassword, Roles.valueOf(newRole)));
            saveUserAccountsToFile();
            //System.out.println("Registration successful!");
            return 3;
        } catch (IOException e) {
            //System.err.println("Error writing to a file: " + e.getMessage());
            return 4;
        } catch (IllegalArgumentException e) {
            //System.err.println("Role can only be Seller and Customer!");
            return 5;
        }
    }

    public int deleteAccount(String email) {
        if (!users.containsKey(email)) {
            //System.out.println("Email not found!");
            return 1;
        }

        users.remove(email);
        saveUserAccountsToFile();
        //System.out.println("Account deleted successfully!");
        return 2;
    }

    public int editAccount(User cUser, String currentEmail, String currentPassword, String newEmailAdd, String newPass) {
        if (!users.containsKey(currentEmail)) {
            //System.out.println("Email not found!");
            return 1;
        }
        User currentUser = cUser; //NEEDS EDIT
        if (newEmailAdd != null) {
            if (!newEmailAdd.isEmpty()) {
                if (!EMAIL_PATTERN.matcher(newEmailAdd).matches()) {
                    //System.out.println("Invalid email format!");
                    return 2;
                }
                if (users.containsKey(newEmailAdd)) {
                    //System.out.println("Email already exists! Please choose a different one.");
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
        //System.out.println("Account updated successfully!");
        return 4;
    }

    public void saveUserAccountsToFile() {
        for (User user : users.values()) {
            user.loadBlockedList();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users.values()) {
                writer.write(user.getEmail() + ";" + user.getPassword() + ";" + user.getUserType().name() + ";" + String
                        .join(",", user.getBlockedUsers().stream().map(s -> s.getEmail()).toArray(String[]::new)) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to a file: " + e.getMessage());
        }
    }
}
