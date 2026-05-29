package patient.management.system.service;

import patient.management.system.dao.LoginDAO;
import patient.management.system.model.User;

public class LoginService {
public User login(String username, String password, String role) {
        try {
            return new LoginDAO().login(username, password, role);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
