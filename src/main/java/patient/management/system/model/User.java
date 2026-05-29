package patient.management.system.model;

import patient.management.system.dao.IdDAO;

public class User {
    private String userId;
    private String username;
    private String name;
    private String password;
    private Role role;

    public User() {}

    public User(String username, String name, String password, Role role) {
        this.userId = IdDAO.getUserId();
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role.toString();
    }

    public String getName() {
        return name;
    }
}
