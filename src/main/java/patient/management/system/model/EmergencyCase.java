package patient.management.system.model;

import patient.management.system.dao.IdDAO;

import java.time.LocalDateTime;

public class EmergencyCase {
    private String emergencyCaseId;
    private String patientId;
    private String temporaryPatientId;
    private boolean isTemporaryPatient;
    private String patientName;
    private int age;
    private String gender;
    private String cnic;
    private LocalDateTime arrivalTime;
    private String initialComplaint;
    private TriageColor triageColor;
    private String triageRemark;
    private Status status;
    private String finalOutcome;

    public enum Status {
        ACTIVE,
        COMPLETE,
        MOVED_TO_ICU,
        MOVED_TO_WARD
    }

    public EmergencyCase(
            String patientId,
            String temporaryPatientId,
            boolean isTemporaryPatient,
            String patientName,
            int age,
            String gender,
            String cnic,
            LocalDateTime arrivalTime,
            String initialComplaint,
            TriageColor triageColor,
            String triageRemark,
            Status status,
            String finalOutcome) {

        this.emergencyCaseId = IdDAO.getEmergencyCaseId();
        this.patientId = patientId;
        this.temporaryPatientId = temporaryPatientId;
        this.isTemporaryPatient = isTemporaryPatient;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.cnic = cnic;
        this.arrivalTime = arrivalTime;
        this.initialComplaint = initialComplaint;
        this.triageColor = triageColor;
        this.triageRemark = triageRemark;
        this.status = status;
        this.finalOutcome = finalOutcome;
    }

    public String getEmergencyCaseId() {
        return emergencyCaseId;
    }

    public void setEmergencyCaseId(String emergencyCaseId) {
        this.emergencyCaseId = emergencyCaseId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTemporaryPatientId() {
        return temporaryPatientId;
    }

    public void setTemporaryPatientId(String temporaryPatientId) {
        this.temporaryPatientId = temporaryPatientId;
    }

    public boolean isTemporaryPatient() {
        return isTemporaryPatient;
    }

    public void setIsTemporaryPatient(boolean isTemporaryPatient) {
        this.isTemporaryPatient = isTemporaryPatient;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getInitialComplaint() {
        return initialComplaint;
    }

    public void setInitialComplaint(String initialComplaint) {
        this.initialComplaint = initialComplaint;
    }

    public TriageColor getTriageColor() {
        return triageColor;
    }

    public void setTriageColor(TriageColor triageColor) {
        this.triageColor = triageColor;
    }

    public String getTriageRemark() {
        return triageRemark;
    }

    public void setTriageRemark(String triageRemark) {
        this.triageRemark = triageRemark;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFinalOutcome() {
        return finalOutcome;
    }

    public void setFinalOutcome(String finalOutcome) {
        this.finalOutcome = finalOutcome;
    }
}
