package patient.management.system.service;

import patient.management.system.dao.*;
import patient.management.system.dto.*;
import patient.management.system.model.*;

import java.util.List;

public class EmergencyTeamService {
    private final EmergencyCaseDAO emergencyCaseDAO = new EmergencyCaseDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final EmergencyTeamDAO emergencyTeamDAO = new EmergencyTeamDAO();

    public List<EmergencyCaseDTO> getTemporaryCases() {
        try {
            return emergencyCaseDAO.getTemporaryPatients();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public int getCompletedEmergencyCasesCount() {
        return emergencyCaseDAO.getCompletedEmergencyCasesCount();
    }

    public int getActiveEmergencyCasesCount() {
        return emergencyCaseDAO.getActiveEmergencyCasesCount();
    }

    public void updateEmergencyMedicalRecord(
            String emergencyCaseId,
            String patientId,
            String temporaryPatientId,
            String handledBy,
            String diagnosis,
            String treatmentGiven,
            String prescription,
            String notes,
            TriageColor triageColor,
            String finalOutcome) {

        EmergencyCase emergencyCase = emergencyCaseDAO.getEmergencyCase(emergencyCaseId);

        if (!emergencyCase.getStatus().toString().equals("ACTIVE")) {
            throw new RuntimeException("Emergency case already completed.");
        }

        medicalRecordDAO.updateEmergencyMedicalRecord(
                emergencyCaseId,
                patientId,
                temporaryPatientId,
                handledBy,
                diagnosis,
                treatmentGiven,
                prescription,
                notes);

        emergencyCaseDAO.completeEmergencyCase(emergencyCaseId, finalOutcome);
    }

    // query -> userId or username
    public EmergencyTeam getEmergencyTeam(String query) {
        try {
            return emergencyTeamDAO.getEmergencyTeam(query);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
