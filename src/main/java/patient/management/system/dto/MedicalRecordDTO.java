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
}