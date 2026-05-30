package patient.management.system.dto;

public class AppointmentReportDTO {
    private String doctorId;
    private String doctorName;
    private int totalAppointments;
    private int completedAppointments;
    private int cancelledAppointments;
    private int rescheduledAppointments;

    public AppointmentReportDTO() {}

    public AppointmentReportDTO(
            String doctorId,
            String doctorName,
            int totalAppointments,
            int completedAppointments,
            int cancelledAppointments,
            int rescheduledAppointments) {

        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.rescheduledAppointments = rescheduledAppointments;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public int getRescheduledAppointments() {
        return rescheduledAppointments;
    }
}