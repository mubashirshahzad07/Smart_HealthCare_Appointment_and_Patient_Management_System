package patient.management.system.service;

import patient.management.system.dao.PatientDAO;
import patient.management.system.dto.PatientDTO;

import java.util.List;

public class PatientService {
    private final PatientDAO patientDAO = new PatientDAO();

    public void addPatient(String name, String gender, int age, String email) {
        try {
            patientDAO.addPatient(name, gender, age, email);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<PatientDTO> getPatients() {
        try {
            return patientDAO.getPatients();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
