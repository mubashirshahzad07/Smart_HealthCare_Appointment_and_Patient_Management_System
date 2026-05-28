package patient.management.system.service;

import patient.management.system.dao.DoctorDAO;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Doctor;

import java.util.List;

public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public void addDoctor(String username, String password, String doctorName, double appointmentFee, Doctor.Specialization specialization) {
        try {
            doctorDAO.addDoctor(username, password, doctorName, appointmentFee, specialization);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorDTO> getActiveDoctors() {
        try {
            return doctorDAO.getActiveDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorDTO> getInactiveDoctors() {
        try {
            return doctorDAO.getInactiveDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id doctor ID or user ID
     */
    public void activateDoctor(String id) {
        try {
            doctorDAO.activateDoctor(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id doctor ID or user ID
     */
    public void inactivateDoctor(String id) {
        try {
            doctorDAO.inactivateDoctor(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}