package patient.management.system.dao;

import patient.management.system.model.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.EmergencyTeamDTO;
import patient.management.system.model.TriageColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyTeamDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File emergencyTeamFile = new File("data/emergency_teams.json");

    public static void main(String[] args) {
        new EmergencyTeamDAO().addEmergencyTeam("green_team", "Green Emergency Team", "green123", TriageColor.GREEN);
    }

    public void addEmergencyTeam(String username, String name, String password, TriageColor triageColor) {
        ArrayList<EmergencyTeam> emergencyTeams = getEmergencyTeamsInternal();
        usernameAvailable(emergencyTeams, username);

        EmergencyTeam newEmergencyTeam = new EmergencyTeam(triageColor, username, password, name);
        new LoginDAO().addUser(newEmergencyTeam.getUserId(), username, password, name, Role.EMERGENCY_TEAM);
        emergencyTeams.add(newEmergencyTeam);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(emergencyTeamFile, emergencyTeams);
        } catch (IOException e) {
            throw new RuntimeException("Unable to register patient.");
        }
    }

    private ArrayList<EmergencyTeam> getEmergencyTeamsInternal() {

        try {
            if (!emergencyTeamFile.exists() || emergencyTeamFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    emergencyTeamFile,
                    new TypeReference<ArrayList<EmergencyTeam>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load emergency teams.");
        }
    }

    public List<EmergencyTeamDTO> getEmergencyTeams() {

        try {

            if (!emergencyTeamFile.exists() || emergencyTeamFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    emergencyTeamFile,
                    new TypeReference<List<EmergencyTeamDTO>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load emergency teams.");
        }
    }

    public EmergencyTeam getEmergencyTeam(String query) {
        ArrayList<EmergencyTeam> teams = getEmergencyTeamsInternal();

        for (EmergencyTeam team : teams) {
            if (team.getUserId().equals(query) || team.getUsername().equals(query)) {
                return team;
            }
        }

        throw new RuntimeException("No team with given userId or username exists.");
    }

    private void usernameAvailable(ArrayList<EmergencyTeam> teams, String username) {
        for (EmergencyTeam team : teams) {
            if (team.getUsername().equals(username)) {
                throw new RuntimeException("Username not available.");
            }
        }
    }
}