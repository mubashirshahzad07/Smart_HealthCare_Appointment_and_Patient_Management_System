package patient.management.system.dto;

public class DoctorDTO {
    private String userId;
    private String username;
    private String name;
    private String doctorId;
    private String department;
    private double appointmentFee;

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

    public String getDepartment() {
        return department;
    }

    public double getAppointmentFee() {
        return appointmentFee;
    }
}
