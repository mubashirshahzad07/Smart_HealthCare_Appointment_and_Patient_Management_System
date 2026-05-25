package patient.management.system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import patient.management.system.dao.IdDao;

@JsonIgnoreProperties({"password", "role"})
public class Admin extends User {
    private String adminId;

    public Admin() {}

    public Admin(String adminName, String username, String password) {
        super(username, adminName, password, Role.ADMIN);
        this.adminId = IdDao.getAdminId();
    }

    public String getAdminId() {
        return adminId;
    }
}