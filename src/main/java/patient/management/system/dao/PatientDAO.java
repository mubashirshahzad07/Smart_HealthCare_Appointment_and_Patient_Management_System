package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.PatientDTO;
import patient.management.system.model.Patient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/patients.json");

    /**
     * @throws RuntimeException if email format is invalid [email format: .....@gmail.com]
     */
    public Patient addPatient(String name, String gender, int age, String email, String cnic) {

        ArrayList<Patient> patients = getPatientsInternal();
        duplicatePatientRegistration(cnic, patients);

        Patient newPatient = new Patient(name, gender, age, email, cnic);
        patients.add(newPatient);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, patients);
            return newPatient;
        } catch (IOException e) {
            throw new RuntimeException("Unable to register patient.");
        }
    }

    private void duplicatePatientRegistration(String cnic, ArrayList<Patient> patients) {
        for (Patient patient : patients) {
            if (cnic.equals(patient.getCnic())) {
                throw new RuntimeException("Patient with the same CNIC is already registered.");
            }
        }
    }

    private ArrayList<Patient> getPatientsInternal() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Patient>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load patients data.");
        }
    }

    /**
     * checks if the patient is registered
     */
    public void patientRegistered(String patientId) {
        PatientDAO patientDAO = new PatientDAO();
        List<PatientDTO> patients = patientDAO.getPatients();

        for (PatientDTO patient : patients) {
            if (patient.getPatientId().equals(patientId)) return;
        }

        throw new RuntimeException("Patient is not registered.");
    }

    public List<PatientDTO> getPatients() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<PatientDTO>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load patients data.");
        }
    }
}