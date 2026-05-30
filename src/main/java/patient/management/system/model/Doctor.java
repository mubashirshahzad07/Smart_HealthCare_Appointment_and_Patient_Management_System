package patient.management.system.model;

import patient.management.system.dao.IdDAO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"password", "role"})
public class Doctor extends User {
    private String doctorId;
    private String name;
    private double appointmentFee;
    private Specialization specialization;
    private boolean isActive;

    public enum Specialization {
        GENERAL_PHYSICIAN,
        CARDIOLOGIST,
        DERMATOLOGIST,
        ORTHOPEDIC,
        PEDIATRICIAN,
        NEUROLOGIST,
        ENT,
        GYNECOLOGIST,
        PSYCHIATRIST
    };

    public Doctor() {}

    public Doctor(String username, String password, String name, double appointmentFee, Specialization specialization) {
        super(username, name, password, Role.DOCTOR);
        this.doctorId = IdDAO.getDoctorId();
        this.name = name;
        this.appointmentFee = appointmentFee;
        this.specialization = specialization;
        this.isActive = true;
    }

    public String getSpecialization() {
        return specialization.toString();
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getName() {
        return name;
    }

    public double getAppointmentFee() {
        return appointmentFee;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setAppointmentFee(double appointmentFee) {
        this.appointmentFee = appointmentFee;
    }
}
