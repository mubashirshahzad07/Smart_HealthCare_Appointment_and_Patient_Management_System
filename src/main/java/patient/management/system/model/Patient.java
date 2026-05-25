package patient.management.system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import patient.management.system.dao.IdDao;
import patient.management.system.service.EmailService;

@JsonIgnoreProperties({"password", "role"})
public class Patient extends User {
    private String patientId;
    private String gender;
    private int age;
    private String email;

    public Patient() {}

    /**
     * @throws RuntimeException if email format is invalid [email format: ....@gmail.com]
     * @param name
     * @param username
     * @param password
     * @param gender
     * @param age
     * @param email
     */
    public Patient(String name, String username, String password, String gender, int age, String email) {
        super(username, name, password, Role.PATIENT);

        this.patientId = IdDao.getPatientId();
        this.gender = gender;
        this.age = age;

        if (EmailService.isValidEmail(email)) {
            this.email = email;
        } else {
            throw new RuntimeException("Invalid email.");
        }
    }

    public String getPatientId() {
        return patientId;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }
}
