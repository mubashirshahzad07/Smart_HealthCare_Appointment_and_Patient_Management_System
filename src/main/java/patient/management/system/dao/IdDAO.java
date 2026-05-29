package patient.management.system.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IdDAO {
    public static String getUserId() {
        return generateId("data/user_id.txt", "USR");
    }

    public static String getAdminId() {
        return generateId("data/admin_id.txt", "ADM");
    }

    public static String getPatientId() {
        return generateId("data/patient_id.txt", "PT");
    }

    public static String getDoctorId() {
        return generateId("data/doctor_id.txt", "DR");
    }

    public static String getAppointmentId() {
        return generateId("data/appointment_id", "APPT");
    }

    public static String getReceptionistId() {
        return generateId("data/receptionist_id", "RECPT");
    }

    public static String getEmergencyTeamId() {
        return generateId("data/emergency_team_id", "EMT");
    }

    public static String getEmergencyCaseId() {
        return generateId("data/emergency_case_id", "EMC");
    }

    public static String getMedicalRecordId() {
        return generateId("data/medical_record_id", "MR");
    }

    public static String getTemporaryPatientId() {
        return generateId("data/temporary_patient_id", "TEMP");
    }

    public static String generateId(String filePath, String idPrefix) {
        Path path = Path.of(filePath);

        try {
            if ((!Files.exists(path)) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "0001");
            }

            String currentId = Files.readString(path).trim();

            if (currentId.isEmpty()) {
                currentId = "0001";
            }

            int nextId = Integer.parseInt(currentId) + 1;
            Files.writeString(path, String.format("%04d", nextId));

            return idPrefix + currentId;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate ID");
        }
    }
}
