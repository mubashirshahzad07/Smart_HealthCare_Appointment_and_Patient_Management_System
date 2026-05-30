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

    public void addEmergencyTeam(String username, String name, String password, TriageColor triageColor) {
        ArrayList<EmergencyTeam> emergencyTeams = getEmergencyTeamsInternal();
        emergencyTeams.add(new EmergencyTeam(triageColor, username, password, name));

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
}