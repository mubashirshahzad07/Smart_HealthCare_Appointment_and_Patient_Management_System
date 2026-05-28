package patient.management.system.dto;

public class DoctorDTO {
    private String userId;
    private String username;
    private String name;
    private String doctorId;
    private String specialization;
    private double appointmentFee;
    private boolean isActive;

    public DoctorDTO() {}

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public double getAppointmentFee() {
        return appointmentFee;
    }
}
