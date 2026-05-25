package patient.management.system.dto;

public class PatientDTO {
    private String userId;
    private String username;
    private String name;
    private String patientId;
    private String gender;
    private int age;
    private String email;
    private boolean willingToReschedule;

    public PatientDTO() {}

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

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

    public boolean getWillingToReschedule() {
        return willingToReschedule;
    }
}
