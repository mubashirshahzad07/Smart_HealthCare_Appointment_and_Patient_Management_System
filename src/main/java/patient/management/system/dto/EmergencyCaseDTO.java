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

    public void setEmergencyCaseId(String emergencyCaseId) {
        this.emergencyCaseId = emergencyCaseId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setTemporaryPatientId(String temporaryPatientId) {
        this.temporaryPatientId = temporaryPatientId;
    }

    public void setIsTemporaryPatient(boolean isTemporaryPatient) {
        this.isTemporaryPatient = isTemporaryPatient;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setInitialComplaint(String initialComplaint) {
        this.initialComplaint = initialComplaint;
    }

    public void setTriageColor(String triageColor) {
        this.triageColor = triageColor;
    }

    public void setTriageRemark(String triageRemark) {
        this.triageRemark = triageRemark;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFinalOutcome(String finalOutcome) {
        this.finalOutcome = finalOutcome;
    }
}