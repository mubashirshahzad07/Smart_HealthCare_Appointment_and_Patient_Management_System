package patient.management.system.dto;

public class ReceptionistDTO {
    private String userId;
    private String username;
    private String name;
    private String receptionistId;
    private String shift;
    private boolean isActive;

    public ReceptionistDTO() {}

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getReceptionistId() {
        return receptionistId;
    }

    public String getShift() {
        return shift;
    }

    public boolean getIsActive() {
        return isActive;
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

    public void setReceptionistId(String receptionistId) {
        this.receptionistId = receptionistId;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}