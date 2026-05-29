package patient.management.system.dao;

import patient.management.system.model.*;

import java.util.ArrayList;
import java.io.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginDAO {
    private ArrayList<User> getUsersInternal() {

    try {
        File usersFile = new File("data/users.json");

        if (!usersFile.exists() || usersFile.length() == 0) {
            return new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                usersFile,
                new TypeReference<ArrayList<User>>() {}
        );

    } catch (IOException e) {
        throw new RuntimeException("Unable to load users data.");
    }
}

    public User login(String username, String password, String role) {
        ArrayList<User> users = getUsersInternal(); 

        for (User user : users) {
            boolean usernameMatch = user.getUsername().equals(username);
            boolean passwordMatch = user.getPassword().equals(password);
            boolean roleMatch = user.getRole().toString().toLowerCase().equals(role.toLowerCase());

            if (usernameMatch && passwordMatch && roleMatch) {
                return user;
            }
        }

        throw new RuntimeException("Invalid username or password.");
    }
}