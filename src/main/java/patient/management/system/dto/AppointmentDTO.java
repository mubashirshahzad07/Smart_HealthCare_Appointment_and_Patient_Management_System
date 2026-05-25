package patient.management.system.dto;

public class AppointmentDTO {
    private String appointmentId;
    private String appointmentDate;
    private int appointmentHour;
    private String patientId;
    private String doctorId;
    private String receptionistId;
    private String patientDescription;
    private String status;
    private String type;
    private boolean willingToReschedule;

    public AppointmentDTO() {}

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public int getAppointmentHour() {
        return appointmentHour;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getReceptionistId() {
        return receptionistId;
    }

    public String getPatientDescription() {
        return patientDescription;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public boolean isWillingToReschedule() {
        return willingToReschedule;
    }
}
