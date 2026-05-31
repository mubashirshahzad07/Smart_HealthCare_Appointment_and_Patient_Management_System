package patient.management.system.model;

import java.time.LocalDateTime;

public class MedicalRecord {

    private String medicalRecordId;
    private String patientId;
    private String temporaryPatientId;
    private String appointmentId;
    private String emergencyCaseId;
    private RecordType recordType;
    private String handledBy;
    private String diagnosis;
    private String treatmentGiven;
    private String prescription;
    private String notes;
    private TriageColor triageColor;
    private LocalDateTime recordDateTime;
    private RecordStatus recordStatus;

    public enum RecordStatus {
        PENDING, 
        COMPLETED
    }

    public enum RecordType {
        REGULAR,
        EMERGENCY
    }

    public MedicalRecord(
            String medicalRecordId,
            String patientId,
            String temporaryPatientId,
            String appointmentId,
            String emergencyCaseId,
            RecordType recordType,
            String handledBy,
            String diagnosis,
            String treatmentGiven,
            String prescription,
            String notes,
            TriageColor triageColor,
            LocalDateTime recordDateTime,
            RecordStatus recordStatus
        ) {

        this.medicalRecordId = medicalRecordId;
        this.patientId = patientId;
        this.temporaryPatientId = temporaryPatientId;
        this.appointmentId = appointmentId;
        this.emergencyCaseId = emergencyCaseId;
        this.recordType = recordType;
        this.handledBy = handledBy;
        this.diagnosis = diagnosis;
        this.treatmentGiven = treatmentGiven;
        this.prescription = prescription;
        this.notes = notes;
        this.triageColor = triageColor;
        this.recordDateTime = recordDateTime;
        this.recordStatus = recordStatus;
    }

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
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

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getEmergencyCaseId() {
        return emergencyCaseId;
    }

    public void setEmergencyCaseId(String emergencyCaseId) {
        this.emergencyCaseId = emergencyCaseId;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentGiven() {
        return treatmentGiven;
    }

    public void setTreatmentGiven(String treatmentGiven) {
        this.treatmentGiven = treatmentGiven;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TriageColor getTriageColor() {
        return triageColor;
    }

    public void setTriageColor(TriageColor triageColor) {
        this.triageColor = triageColor;
    }

    public String getRecordDateTime() {
        return recordDateTime.toString();
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        this.recordDateTime = recordDateTime;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }
}