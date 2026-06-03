package patient.management.system.dto;

import patient.management.system.model.*;

import java.util.*;

public class DoctorDTO {
    private String userId;
    private String username;
    private String name;
    private String doctorId;
    private String specialization;
    private double appointmentFee;
    private boolean isActive;
    private List<DoctorSchedule> schedules;

    public DoctorDTO() {}

    public String getUserId() {
        return userId;
    }

    public List<DoctorSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<DoctorSchedule> schedules) {
        this.schedules = schedules;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setAppointmentFee(double appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
