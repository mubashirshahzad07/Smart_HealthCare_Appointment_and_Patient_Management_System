package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Doctor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/doctors.json");

    public static void main(String[] args) {
        new DoctorDAO().addDoctor("username", "password", "name", "department", 2345);
    }

    public void addDoctor(String username, String password, String doctorName, String department, double appointmentFee) {
        Doctor newDoctor = new Doctor(username, password, doctorName, department, appointmentFee);

        ArrayList<Doctor> doctors = getDoctorsInternal();
        doctors.add(newDoctor);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, doctors);

        } catch (IOException e) {
            throw new RuntimeException("Unable to register doctor.");
        }
    }

    private ArrayList<Doctor> getDoctorsInternal() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Doctor>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    public List<DoctorDTO> getDoctors() {

        try {
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<List<DoctorDTO>>(){}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }
}
