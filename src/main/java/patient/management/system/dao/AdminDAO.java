package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import patient.management.system.dto.AdminDTO;
import patient.management.system.model.Admin;
import patient.management.system.model.Role;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdminDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/admins.json");

    public void addAdmin(String name, String username, String password) {

        ArrayList<Admin> admins = getAdminsInternal();
        usernameAvailable(admins, username);

        Admin newAdmin = new Admin(name, username, password);
        new LoginDAO().addUser(newAdmin.getUserId(), username, password, name, Role.ADMIN);
        admins.add(newAdmin);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, admins);

        } catch (IOException e) {
            throw new RuntimeException("Unable to register Admin.");
        }
    }

    private ArrayList<Admin> getAdminsInternal() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    new File("data/admins.json"),
                    new TypeReference<ArrayList<Admin>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load Admins data.");
        }
    }

    public ArrayList<AdminDTO> getAdmins() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    new File("data/admins.json"),
                    new TypeReference<ArrayList<AdminDTO>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load Admins data.");
        }
    }

    private void usernameAvailable(ArrayList<Admin> admins, String username) {
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username)) {
                throw new RuntimeException("Username not available.");
            }
        }
    }
}
