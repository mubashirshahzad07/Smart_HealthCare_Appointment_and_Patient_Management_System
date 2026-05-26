package patient.management.system.model;

import patient.management.system.dao.IdDao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"password", "role"})
public class Doctor extends User {
    private String doctorId;
    private String name;
    private String department;
    private double appointmentFee;
    private Specialization specialization;

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

    public Doctor(String username, String password, String name, String department, double appointmentFee, Specialization specialization) {
        super(username, name, password, Role.DOCTOR);
        this.doctorId = IdDao.getDoctorId();
        this.name = name;
        this.department = department;
        this.appointmentFee = appointmentFee;
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization.toString();
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDepartment() {
        return department;
    }

    public String getName() {
        return name;
    }

    public double getAppointmentFee() {
        return appointmentFee;
    }
}
