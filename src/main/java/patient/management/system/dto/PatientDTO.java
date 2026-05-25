package patient.management.system.dto;

public class PatientDTO {
    private String name;
    private String patientId;
    private String gender;
    private int age;
    private String email;

    public PatientDTO() {}

    public String getName() {
        return name;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getGender() {
        return gender;
    }

    public double getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }
}
