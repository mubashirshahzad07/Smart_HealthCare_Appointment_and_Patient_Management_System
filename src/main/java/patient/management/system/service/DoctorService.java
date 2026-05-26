package patient.management.system.service;

import patient.management.system.dao.DoctorDAO;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Doctor;

import java.util.List;

public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public void addDoctor(String username, String password, String doctorName, String department, double appointmentFee, Doctor.Specialization specialization) {
        try {
            doctorDAO.addDoctor(username, password, doctorName, department, appointmentFee, specialization);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorDTO> getDoctors() {
        try {
            return doctorDAO.getDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
