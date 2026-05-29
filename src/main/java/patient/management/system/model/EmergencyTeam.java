package patient.management.system.model;

import patient.management.system.dao.IdDAO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"password", "role"})
public class EmergencyTeam extends User { 
    private String teamId;
    private TriageColor triageColor;

    public EmergencyTeam(TriageColor triageColor, String username, String password, String name) {
        super(username, name, password, Role.EMERGENCY_TEAM);
        this.teamId = IdDAO.getEmergencyTeamId();
        this.triageColor = triageColor;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTriageColor() {
        return triageColor.toString();
    }
}
