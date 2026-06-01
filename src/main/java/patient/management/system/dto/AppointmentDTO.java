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
    private String doctorName;
    private String patientName;

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

    public String getDoctorName() {
        return doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentHour(int appointmentHour) {
        this.appointmentHour = appointmentHour;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setReceptionistId(String receptionistId) {
        this.receptionistId = receptionistId;
    }

    public void setPatientDescription(String patientDescription) {
        this.patientDescription = patientDescription;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWillingToReschedule(boolean willingToReschedule) {
        this.willingToReschedule = willingToReschedule;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}