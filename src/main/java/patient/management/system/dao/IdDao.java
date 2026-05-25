package patient.management.system.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IdDao {
    public static String getUserId() {
        return generateId("data/user_id.txt", "USER-");
    }

    public static String getAdminId() {
        return generateId("data/admin_id.txt", "ADM-");
    }

    public static String getPatientId() {
        return generateId("data/patient_id.txt", "PAT-");
    }

    public static String getDoctorId() {
        return generateId("data/doctor_id.txt", "DOCT-");
    }

    public static String getAppointmentId() {
        return generateId("data/appointment_id", "APPT-");
    }

    public static String getReceptionistId() {
        return generateId("data/receptionist_id", "RECEP-");
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
