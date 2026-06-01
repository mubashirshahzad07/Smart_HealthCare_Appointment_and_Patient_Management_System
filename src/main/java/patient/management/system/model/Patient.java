package patient.management.system.model;

import patient.management.system.dao.IdDAO;

public class Patient {
    private String patientId;
    private String gender;
    private String name;
    private int age;
    private String email;
    private String cnic;

    public Patient() {}

    /**
     * @throws RuntimeException if email format is invalid [email format: ....@gmail.com]
     */
    public Patient(String name, String gender, int age, String email, String cnic) {
        this.patientId = IdDAO.getPatientId();
        this.gender = gender;
        this.age = age;
        this.name = name;
        this.cnic = cnic;
        this.email = email;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getCnic() {
        return cnic;
    }
}
