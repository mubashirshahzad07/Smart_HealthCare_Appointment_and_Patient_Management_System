package patient.management.system.model;

import patient.management.system.dao.IdDao;

import java.time.LocalDate;

public class Appointment {
    private String appointmentId;
    private String appointmentDate;
    private int appointmentHour; // 24-hours time format
    private String patientId;
    private String doctorId;
    private String receptionistId;
    private String patientDescription;
    private Status status;
    private Type type;
    private boolean willingToReschedule;

    public enum Type {
        REGULAR,
        EMERGENCY
    }

    public enum Status {
        SCHEDULED,
        RESCHEDULED,
        COMPLETED,
        IN_PROGRESS
    }

    public Appointment() {}

    public Appointment(
            int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,String patientId,
            String doctorId, String receptionistId, String patientDescription, Status status, Type type, boolean willingToReschedule
    ) {

        this.appointmentId = IdDao.getAppointmentId();
        this.appointmentDate = String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay);
        this.appointmentHour = appointmentHour;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.receptionistId = receptionistId;
        this.patientDescription = patientDescription;
        this.status = status;
        this.type = type;
        this.willingToReschedule = willingToReschedule;
    }

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
        return status.toString();
    }

    public String getType() {
        return type.toString();
    }

    public boolean getWillingToReschedule() {
        return willingToReschedule;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentHour(int appointmentHour) {
        this.appointmentHour = appointmentHour;
    }

    public void setWillingToReschedule(boolean willingToReschedule) {
        this.willingToReschedule = willingToReschedule;
    }

    public void setReceptionistId(String receptionistId) {
        this.receptionistId = receptionistId;
    }
}
