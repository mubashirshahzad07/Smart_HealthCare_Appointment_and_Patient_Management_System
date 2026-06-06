package patient.management.system.dao;

import patient.management.system.model.*;
import patient.management.system.dto.*;

import java.util.ArrayList;
import java.io.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/users.json");

    public void addUser(String userId, String username, String password, String name, Role role) {
        ArrayList<User> users = getUsersInternal();

        User newUser = new User(userId, username, name, password, role);

        users.add(newUser);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (IOException e) {
            throw new RuntimeException("Unable to register user.");
        }
    }

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
            boolean roleMatch = user.getRole().equalsIgnoreCase(role);

            if (usernameMatch && passwordMatch && roleMatch) {

                if (role.equalsIgnoreCase("DOCTOR")) {
                    DoctorDTO doctor = new DoctorDAO().getDoctorByUserId(user.getUserId());

                    if (!doctor.getIsActive()) {
                        throw new RuntimeException("Doctor account is inactive.");
                    }
                }

                if (role.equalsIgnoreCase("RECEPTIONIST")) {
                    Receptionist receptionist = new ReceptionistDAO().getReceptionistByUserId(user.getUserId());

                    if (!receptionist.getIsActive()) {
                        throw new RuntimeException("Receptionist account is inactive.");
                    }
                }

                return user;
            }
        }

        throw new RuntimeException("Invalid username or password.");
    }
}