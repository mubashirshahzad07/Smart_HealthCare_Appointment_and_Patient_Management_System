package patient.management.system.dto;

public class MedicalRecordDTO {

    private String medicalRecordId;
    private String patientId;
    private String temporaryPatientId;
    private String appointmentId;
    private String emergencyCaseId;
    private String recordType;
    private String handledBy;
    private String diagnosis;
    private String treatmentGiven;
    private String prescription;
    private String notes;
    private String triageColor;
    private String recordDateTime;
    private String recordStatus;

    public MedicalRecordDTO() {}

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getTemporaryPatientId() {
        return temporaryPatientId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getEmergencyCaseId() {
        return emergencyCaseId;
    }

    public String getRecordType() {
        return recordType;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatmentGiven() {
        return treatmentGiven;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getNotes() {
        return notes;
    }

    public String getTriageColor() {
        return triageColor;
    }

    public String getRecordDateTime() {
        return recordDateTime;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setTemporaryPatientId(String temporaryPatientId) {
        this.temporaryPatientId = temporaryPatientId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setEmergencyCaseId(String emergencyCaseId) {
        this.emergencyCaseId = emergencyCaseId;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setTreatmentGiven(String treatmentGiven) {
        this.treatmentGiven = treatmentGiven;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTriageColor(String triageColor) {
        this.triageColor = triageColor;
    }

    public void setRecordDateTime(String recordDateTime) {
        this.recordDateTime = recordDateTime;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
}