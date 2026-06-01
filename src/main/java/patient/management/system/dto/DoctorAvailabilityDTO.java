package patient.management.system.dto;

import java.util.List;

public class DoctorAvailabilityDTO {

    private String doctorId;
    private String doctorName;
    private String specialization;
    private double appointmentFee;
    private List<Integer> freeSlots;

    public DoctorAvailabilityDTO() {}

    public DoctorAvailabilityDTO(
            String doctorId,
            String doctorName,
            String specialization,
            double appointmentFee,
            List<Integer> freeSlots
    ) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.appointmentFee = appointmentFee;
        this.freeSlots = freeSlots;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public double getAppointmentFee() {
        return appointmentFee;
    }

    public List<Integer> getFreeSlots() {
        return freeSlots;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setAppointmentFee(double appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    public void setFreeSlots(List<Integer> freeSlots) {
        this.freeSlots = freeSlots;
    }
}