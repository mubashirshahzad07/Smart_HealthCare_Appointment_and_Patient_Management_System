package patient.management.system.dao;

import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Doctor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/doctors.json");

    public void addDoctor(
            String username, String password, String doctorName,
            double appointmentFee, Doctor.Specialization specialization) {

        ArrayList<Doctor> doctors = getAllDoctorsInternal();
        usernameAvailable(doctors, username);

        Doctor newDoctor = new Doctor(username, password, doctorName, appointmentFee, specialization);

        doctors.add(newDoctor);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, doctors);
        } catch (IOException e) {
            throw new RuntimeException("Unable to register doctor.");
        }
    }

    private ArrayList<Doctor> getActiveDoctorsInternal() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<Doctor> doctors = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Doctor>>() {
                    });

            doctors.removeIf(doctor -> !doctor.getIsActive());

            return doctors;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    /**
     * @return all the active and inactive doctors
     */
    private ArrayList<Doctor> getAllDoctorsInternal() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Doctor>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    public ArrayList<DoctorDTO> getAllDoctors() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<DoctorDTO>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    private void usernameAvailable(List<Doctor> doctors, String username) {
        for (Doctor doctor : doctors) {
            if (username.equals(doctor.getUsername())) {
                throw new RuntimeException("Username not available.");
            }
        }
    }

    public List<DoctorDTO> getActiveDoctors() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<DoctorDTO> doctors = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<DoctorDTO>>() {
                    });

            doctors.removeIf(doctor -> !doctor.getIsActive());

            return doctors;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    private ArrayList<Doctor> getInactiveDoctorsInternal() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<Doctor> doctors = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<Doctor>>() {
                    });

            doctors.removeIf(doctor -> doctor.getIsActive());

            return doctors;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    public List<DoctorDTO> getInactiveDoctors() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            ArrayList<DoctorDTO> doctors = mapper.readValue(
                    file,
                    new TypeReference<ArrayList<DoctorDTO>>() {
                    });

            doctors.removeIf(doctor -> doctor.getIsActive());

            return doctors;

        } catch (IOException e) {
            throw new RuntimeException("Unable to load doctors data.");
        }
    }

    /**
     * @param id doctorId or userId
     */
    public void activateDoctor(String id) {
        ArrayList<Doctor> inactiveDoctors = getInactiveDoctorsInternal();

        for (Doctor activeDoctor : inactiveDoctors) {
            if (activeDoctor.getDoctorId().equals(id) || activeDoctor.getUserId().equals(id)) {

                activeDoctor.setIsActive(true);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, inactiveDoctors);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to activate doctor.");
                }

                return;
            }
        }

        throw new RuntimeException("Doctor is unregistered or active.");
    }

    /**
     * @param id doctorId or userId
     */
    public void inactivateDoctor(String id) {
        ArrayList<Doctor> activeDoctors = getActiveDoctorsInternal();

        for (Doctor activeDoctor : activeDoctors) {
            if (activeDoctor.getDoctorId().equals(id) || activeDoctor.getUserId().equals(id)) {

                activeDoctor.setIsActive(false);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, activeDoctors);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to inactivate doctor.");
                }

                return;
            }
        }

        throw new RuntimeException("Doctor is unregistered or inactive.");
    }

    public double getDoctorAppointmentFee(String doctorId) {
        ArrayList<Doctor> doctors = getActiveDoctorsInternal();

        for (Doctor doctor : doctors) {
            if (doctorId.equals(doctor.getDoctorId())) {
                return doctor.getAppointmentFee();
            }
        }

        return 0;
    }

    public void updateDoctorFee(String doctorId, double fee) {
        ArrayList<Doctor> doctors = getAllDoctorsInternal();

        for (Doctor doctor : doctors) {
            if (doctor.getDoctorId().equals(doctorId)) {
                doctor.setAppointmentFee(fee);
            }
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, doctors);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update doctor's appointment fee.");
        }
    }
}
