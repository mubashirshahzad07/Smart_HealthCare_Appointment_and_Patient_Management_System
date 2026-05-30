package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.MedicalRecordDTO;
import patient.management.system.model.MedicalRecord;
import patient.management.system.model.TriageColor;
import patient.management.system.model.MedicalRecord.RecordType;
import patient.management.system.model.MedicalRecord.Record_Status;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class MedicalRecordDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File medicalRecordsFile = new File("data/medical_records.json");

    public void addEmergencyMedicalRecord(
            String emergencyCaseId,
            String patientId,
            String temporaryPatientId,
            String handledBy,
            String diagnosis,
            String treatmentGiven,
            String prescription,
            String notes,
            TriageColor triageColor
    ) {

        ArrayList<MedicalRecord> medicalRecords = getMedicalRecordsInternal();

        MedicalRecord medicalRecord =
                new MedicalRecord(
                        IdDAO.getMedicalRecordId(),
                        patientId,
                        temporaryPatientId,
                        null,
                        emergencyCaseId,
                        MedicalRecord.RecordType.EMERGENCY,
                        handledBy,
                        diagnosis,
                        treatmentGiven,
                        prescription,
                        notes,
                        triageColor,
                        LocalDateTime.now(),
                        (triageColor == TriageColor.BLACK) ? Record_Status.COMPLETED : Record_Status.PENDING
                );

        medicalRecords.add(medicalRecord);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, medicalRecords);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create medical record.");
        }
    }

    public void updateEmergencyMedicalRecord(
            String emergencyCaseId,
            String patientId,
            String temporaryPatientId,
            String handledBy,
            String diagnosis,
            String treatmentGiven,
            String prescription,
            String notes
    ) {

        ArrayList<MedicalRecord> medicalRecords = getMedicalRecordsInternal();

        for (MedicalRecord medicalRecord : medicalRecords) {
            if (medicalRecord.getEmergencyCaseId().equals(emergencyCaseId)) {
                medicalRecord.setHandledBy(handledBy);
                medicalRecord.setDiagnosis(diagnosis);
                medicalRecord.setTreatmentGiven(treatmentGiven);
                medicalRecord.setPrescription(prescription);
                medicalRecord.setNotes(notes);
                medicalRecord.setRecordDateTime(LocalDateTime.now());
                medicalRecord.setRecordStatus(Record_Status.COMPLETED);
            }
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, medicalRecords);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create medical record.");
        }
    }


    private ArrayList<MedicalRecord> getMedicalRecordsInternal() {

        try {
            if (!medicalRecordsFile.exists() || medicalRecordsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    medicalRecordsFile,
                    new TypeReference<ArrayList<MedicalRecord>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load medical records."
            );
        }
    }

    public List<MedicalRecordDTO> getMedicalRecords() {

        try {
            if (!medicalRecordsFile.exists() || medicalRecordsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    medicalRecordsFile,
                    new TypeReference<List<MedicalRecordDTO>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException("Unable to load medical records.");
        }
    }

    public List<MedicalRecordDTO> getPatientMedicalHistory(String patientId) {

        List<MedicalRecordDTO> medicalRecords = getMedicalRecords();
        medicalRecords.removeIf(medicalRecord -> !medicalRecord.getPatientId().equals(patientId));

        return medicalRecords;
    }

    public void updateTemporaryPatientLink(String temporaryPatientId, String patientId) {

        ArrayList<MedicalRecord> medicalRecords = getMedicalRecordsInternal();

        for (MedicalRecord medicalRecord : medicalRecords) {
            if (temporaryPatientId.equals(medicalRecord.getTemporaryPatientId())) {
                medicalRecord.setPatientId(patientId);
            }
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, medicalRecords);
        } catch (IOException e) {
            throw new RuntimeException("Unable to update linked patient.");
        }
    }

    public int getCompletedCasesTodayCount(TriageColor triagecolor) {

        return (int) getMedicalRecordsInternal()
                .stream()
                .filter(record -> record.getRecordType() == RecordType.EMERGENCY)
                .filter(record -> record.getTriageColor() == triagecolor)
                .filter(record -> record.getRecordStatus() == Record_Status.COMPLETED)
                .filter(record -> LocalDateTime.parse(record.getRecordDateTime()).toLocalDate().equals(LocalDate.now()))
                .count();
    }
}