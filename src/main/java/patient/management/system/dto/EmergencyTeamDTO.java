package patient.management.system.dto;

public class EmergencyTeamDTO {
    private String teamId;
    private String triageColor;
    private String name;
    private String username;
    private String userId;

    public EmergencyTeamDTO() {}

    public String getTeamId() {
        return teamId;
    }

    public String getTriageColor() {
        return triageColor;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }
}