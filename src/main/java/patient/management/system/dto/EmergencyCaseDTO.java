package patient.management.system.dto;

public class EmergencyCaseDTO {
    private String emergencyCaseId;
    private String patientId;
    private String temporaryPatientId;
    private boolean isTemporaryPatient;
    private String patientName;
    private int age;
    private String gender;
    private String cnic;
    private String arrivalTime;
    private String initialComplaint;
    private String triageColor;
    private String triageRemark;
    private String status;
    private String finalOutcome;

    public EmergencyCaseDTO() {}

    public String getEmergencyCaseId() {
        return emergencyCaseId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getTemporaryPatientId() {
        return temporaryPatientId;
    }

    public boolean getIsTemporaryPatient() {
        return isTemporaryPatient;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getCnic() {
        return cnic;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getInitialComplaint() {
        return initialComplaint;
    }

    public String getTriageColor() {
        return triageColor;
    }

    public String getTriageRemark() {
        return triageRemark;
    }

    public String getStatus() {
        return status;
    }

    public String getFinalOutcome() {
        return finalOutcome;
    }
}