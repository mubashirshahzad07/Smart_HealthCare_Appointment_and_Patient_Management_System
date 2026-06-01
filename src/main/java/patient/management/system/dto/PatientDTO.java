package patient.management.system.dto;

public class PatientDTO {
    private String name;
    private String patientId;
    private String gender;
    private int age;
    private String email;
    private String cnic;

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

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getCnic() {
        return cnic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
}