package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.EmergencyCaseDTO;
import patient.management.system.dto.EmergencyReportDTO;
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
        } else {

            new MedicalRecordDAO().addEmergencyMedicalRecord(
                    emergencyCase.getEmergencyCaseId(),
                    patientId,
                    temporaryPatientId,
                    null,
                    null,
                    null,
                    null,
                    null,
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

    public List<EmergencyCaseDTO> getEmergencyCasesByTriageColor(TriageColor triageColor) {

        return getEmergencyCases().stream()
                .filter(c -> c.getTriageColor().equalsIgnoreCase(triageColor.name()))
                .toList();
    }

    public List<EmergencyCaseDTO> getCasesByTriageColor(String triageColor) {
        return getEmergencyCases()
                .stream()
                .filter(emergencyCase -> emergencyCase.getTriageColor().equalsIgnoreCase(triageColor))
                .toList();
    }

    public int getCompletedEmergencyCasesCount(TriageColor triageColor) {
        List<EmergencyCaseDTO> cases = getEmergencyCases();
        List<EmergencyCaseDTO> colorCases = cases.stream()
                        .filter(c -> c.getTriageColor().equalsIgnoreCase(triageColor.name()))
                        .toList();

        int count = 0;

        for (EmergencyCaseDTO emergencyCase : colorCases) {
            if (emergencyCase.getStatus().equals("COMPLETE")) {
                count++;
            }
        }

        return count;
    }

    public int getActiveEmergencyCasesCount(TriageColor triageColor) {
        List<EmergencyCaseDTO> cases = getEmergencyCases();
        List<EmergencyCaseDTO> colorCases = cases.stream()
                        .filter(c -> c.getTriageColor().equalsIgnoreCase(triageColor.name()))
                        .toList();

        int count = 0;

        for (EmergencyCaseDTO emergencyCase : colorCases) {
            if (emergencyCase.getStatus().equals("ACTIVE")) {
                count++;
            }
        }

        return count;
    }

    public List<EmergencyCaseDTO> getTemporaryPatients() {

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

    public void linkTemporaryPatient(String emergencyId, String patientId) {

        new PatientDAO().patientRegistered(patientId);

        ArrayList<EmergencyCase> emergencyCases = getEmergencyCasesInternal();

        for (EmergencyCase emergencyCase : emergencyCases) {
            if (emergencyCase.getEmergencyCaseId().equals(emergencyId)) {
                emergencyCase.setPatientId(patientId);
                emergencyCase.setIsTemporaryPatient(false);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(emergencyCasesFile, emergencyCases);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to link patient.");
                }

                new MedicalRecordDAO().updateTemporaryPatientLink(emergencyCase.getTemporaryPatientId(),patientId);

                return;
            }
        }

        throw new RuntimeException("Emergency case not found.");
    }

    public void registerAndLinkTemporaryPatient(String emergencyId, String patientName, int age, String gender, String phoneNumber, String cnic, String email) {
        String patientId = new PatientDAO().addPatient(patientName, gender, age, email, cnic).getPatientId();

        linkTemporaryPatient(emergencyId, patientId);
    }

    public int getTemporaryLinksCount() {
        return (int) getEmergencyCases()
                .stream()
                .filter(emergencyCase -> emergencyCase.getIsTemporaryPatient())
                .count();
    }

    public List<EmergencyCaseDTO> searchTemporaryPatients(String query) {
        String lowerQuery = query.toLowerCase();

        return getTemporaryPatients()
                .stream()
                .filter(emergencyCase -> {

                    boolean matchesName = emergencyCase.getPatientName() != null && emergencyCase.getPatientName().toLowerCase().contains(lowerQuery);
                    boolean matchesCNIC = emergencyCase.getCnic() != null && emergencyCase.getCnic().toLowerCase().contains(lowerQuery);
                    boolean matchesCaseId = emergencyCase.getEmergencyCaseId() != null && emergencyCase.getEmergencyCaseId().toLowerCase().contains(lowerQuery);

                    return matchesName || matchesCNIC || matchesCaseId;
                })
                .toList();
    }

    public List<EmergencyReportDTO> getEmergencyReport() {

        List<EmergencyCaseDTO> cases = getEmergencyCases();
        List<EmergencyReportDTO> report = new ArrayList<>();

        for (TriageColor triageColor : TriageColor.values()) {

            List<EmergencyCaseDTO> colorCases = cases.stream()
                    .filter(c -> c.getTriageColor().equalsIgnoreCase(triageColor.name()))
                    .toList();

            int totalCases = colorCases.size();

            int completed = (int) colorCases.stream()
                    .filter(c -> "COMPLETE".equalsIgnoreCase(c.getStatus()))
                    .count();

            int movedToICU = (int) colorCases.stream()
                    .filter(c -> "moved to icu".equalsIgnoreCase(c.getFinalOutcome()))
                    .count();

            int deceased = (int) colorCases.stream()
                    .filter(c -> "DECEASED".equalsIgnoreCase(c.getFinalOutcome()))
                    .count();

            report.add(new EmergencyReportDTO(triageColor.name(), totalCases, completed, movedToICU, deceased));
        }

        return report;
    }

    public int getMovedToICUCount(TriageColor triageColor) {
        List<EmergencyCaseDTO> cases = getEmergencyCases();
        List<EmergencyCaseDTO> colorCases = cases.stream()
                .filter(c -> c.getTriageColor().equalsIgnoreCase(triageColor.name()))
                .toList();

        return (int) colorCases.stream()
                .filter(c -> "moved to icu".equalsIgnoreCase(c.getFinalOutcome()))
                .count();
    }

    public int getMovedToWardCount(TriageColor triageColor) {
        List<EmergencyCaseDTO> cases = getEmergencyCases();
        List<EmergencyCaseDTO> colorCases = cases.stream()
                .filter(c -> c.getTriageColor().equalsIgnoreCase(triageColor.name()))
                .toList();

        return (int) colorCases.stream()
                .filter(c -> "moved to ward".equalsIgnoreCase(c.getFinalOutcome()))
                .count();
    }
}