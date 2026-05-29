package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.EmergencyCaseDTO;
import patient.management.system.model.EmergencyCase;
import patient.management.system.model.TriageColor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmergencyCaseDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File emergencyCasesFile = new File("data/emergency_cases.json");
    private final PatientDAO patientDAO = new PatientDAO();

    public void addEmergencyCase(
            String patientId,
            boolean isTemporaryPatient,
            String patientName,
            int age,
            String gender,
            String cnic,
            String initialComplaint,
            TriageColor triageColor,
            String triageRemark) {

        ArrayList<EmergencyCase> emergencyCases = getEmergencyCasesInternal();

        String temporaryPatientId = null;

        if (isTemporaryPatient) {
            temporaryPatientId = IdDAO.getTemporaryPatientId();
        } else {
            patientDAO.patientRegistered(patientId);
        }

        EmergencyCase.Status status = EmergencyCase.Status.ACTIVE;
        String finalOutcome = "";

        if (triageColor == TriageColor.BLACK) {
            status = EmergencyCase.Status.COMPLETE;
            finalOutcome = "DECEASED";
        }

        EmergencyCase emergencyCase = new EmergencyCase(
                patientId,
                temporaryPatientId,
                isTemporaryPatient,
                patientName,
                age,
                gender,
                cnic,
                LocalDateTime.now(),
                initialComplaint,
                triageColor,
                triageRemark,
                status,
                finalOutcome);

        emergencyCases.add(emergencyCase);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(emergencyCasesFile, emergencyCases);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create emergency case.");
        }

        if (triageColor == TriageColor.BLACK) {
            new MedicalRecordDAO().addEmergencyMedicalRecord(
                    emergencyCase.getEmergencyCaseId(),
                    patientId,
                    temporaryPatientId,
                    "SYSTEM",
                    "Patient declared deceased",
                    "None",
                    "None",
                    triageRemark,
                    triageColor);
        }
    }

    private ArrayList<EmergencyCase> getEmergencyCasesInternal() {

        try {
            if (!emergencyCasesFile.exists() || emergencyCasesFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    emergencyCasesFile,
                    new TypeReference<ArrayList<EmergencyCase>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load emergency cases.");
        }
    }

    public List<EmergencyCaseDTO> getEmergencyCases() {

        try {

            if (!emergencyCasesFile.exists() || emergencyCasesFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    emergencyCasesFile,
                    new TypeReference<List<EmergencyCaseDTO>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load emergency cases.");
        }
    }

    public List<EmergencyCaseDTO> getCasesByTriageColor(String triageColor) {
        return getEmergencyCases()
                .stream()
                .filter(emergencyCase -> emergencyCase.getTriageColor().equalsIgnoreCase(triageColor))
                .toList();
    }

    public int getCompletedEmergencyCasesCount() {
        List<EmergencyCaseDTO> emergencyCases = getEmergencyCases();

        int count = 0;

        for (EmergencyCaseDTO emergencyCase : emergencyCases) {
            if (emergencyCase.getStatus().equals("COMPLETE")) {
                count++;
            }
        }

        return count;
    }

    public int getActiveEmergencyCasesCount() {
        List<EmergencyCaseDTO> emergencyCases = getEmergencyCases();

        int count = 0;

        for (EmergencyCaseDTO emergencyCase : emergencyCases) {
            if (emergencyCase.getStatus().equals("ACTIVE")) {
                count++;
            }
        }

        return count;
    }

    public List<EmergencyCaseDTO> getTemporaryCases() {

        List<EmergencyCaseDTO> cases = getEmergencyCases();
        cases.removeIf(emergencyCase -> !emergencyCase.getIsTemporaryPatient());

        return cases;
    }

    public EmergencyCase getEmergencyCase(String emergencyCaseId) {
        ArrayList<EmergencyCase> emergencyCases = getEmergencyCasesInternal();

        for (EmergencyCase emergencyCase : emergencyCases) {
            if (emergencyCase.getEmergencyCaseId().equals(emergencyCaseId)) {
                return emergencyCase;
            }
        }

        throw new RuntimeException("Emergency case not found.");
    }

    public void completeEmergencyCase(String emergencyCaseId, String finalOutcome) {

        ArrayList<EmergencyCase> emergencyCases = getEmergencyCasesInternal();

        for (EmergencyCase emergencyCase : emergencyCases) {
            if (emergencyCase.getEmergencyCaseId().equals(emergencyCaseId)) {
                emergencyCase.setStatus(EmergencyCase.Status.COMPLETE);
                emergencyCase.setFinalOutcome(finalOutcome);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(emergencyCasesFile, emergencyCases);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to complete emergency case.");
                }

                return;
            }
        }

        throw new RuntimeException("Emergency case not found.");
    }

    /**
     * @param emergencyId temp id or emergency case id
     * @param patientId 
     */
    public void linkTemporaryPatient(String emergencyId, String patientId) {

        new PatientDAO().patientRegistered(patientId);

        ArrayList<EmergencyCase> emergencyCases = getEmergencyCasesInternal();

        for (EmergencyCase emergencyCase : emergencyCases) {
            if (emergencyCase.getEmergencyCaseId().equals(emergencyId) || emergencyCase.getTemporaryPatientId().equals(emergencyId)) {
                emergencyCase.setPatientId(patientId);
                emergencyCase.setIsTemporaryPatient(false);
                emergencyCase.setTemporaryPatientId(null);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(emergencyCasesFile, emergencyCases);

                } catch (IOException e) {
                    throw new RuntimeException("Unable to link patient.");
                }

                new MedicalRecordDAO()
                        .updateTemporaryPatientLink(
                                emergencyCase.getTemporaryPatientId(),
                                patientId);

                return;
            }
        }

        throw new RuntimeException("Emergency case not found.");
    }

    /**
     * @param emergencyId temp id or emergency case id
     * @param patientName
     * @param age
     * @param gender
     * @param phoneNumber
     * @param cnic
     * @param email
     */
    public void registerAndLinkTemporaryPatient(String emergencyId, String patientName, int age, String gender, String phoneNumber, String cnic, String email) {
        String patientId = new PatientDAO().addPatient(patientName, gender, age, email, cnic).getPatientId();

        linkTemporaryPatient(emergencyId, patientId);
    }
}