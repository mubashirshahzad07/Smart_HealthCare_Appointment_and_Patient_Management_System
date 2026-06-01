package patient.management.system.dto;

public class AdminDTO {
    private String userId;
    private String username;
    private String name;
    private String adminId;

    public AdminDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
