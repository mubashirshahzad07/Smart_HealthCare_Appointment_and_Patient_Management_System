package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import patient.management.system.dto.MedicalRecordDTO;
import patient.management.system.model.MedicalRecord;
import patient.management.system.model.TriageColor;
import patient.management.system.model.MedicalRecord.RecordType;
import patient.management.system.model.MedicalRecord.RecordStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class MedicalRecordDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File medicalRecordsFile = new File("data/medical_records.json");

    public void addRegularMedicalRecord(
            String appointmentId,
            String patientId,
            String handledBy
    ) {

        ArrayList<MedicalRecord> medicalRecords = getMedicalRecordsInternal();

        MedicalRecord medicalRecord =
                new MedicalRecord(
                        IdDAO.getMedicalRecordId(),
                        patientId,
                        null,
                        appointmentId,
                        null,
                        MedicalRecord.RecordType.REGULAR,
                        handledBy,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        RecordStatus.PENDING
                );

        medicalRecords.add(medicalRecord);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, medicalRecords);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create medical record.");
        }
    }

    public void updateRegularMedicalRecord(
        String appointmentId,
        String diagnosis,
        String treatmentGiven,
        String prescription,
        String notes
    ) {

        ArrayList<MedicalRecord> medicalRecords = getMedicalRecordsInternal();

        for (MedicalRecord medicalRecord : medicalRecords) {
            if (appointmentId.equals(medicalRecord.getAppointmentId())) {

                medicalRecord.setDiagnosis(diagnosis);
                medicalRecord.setTreatmentGiven(treatmentGiven);
                medicalRecord.setPrescription(prescription);
                medicalRecord.setNotes(notes);
                medicalRecord.setRecordStatus(RecordStatus.COMPLETED);
                medicalRecord.setRecordDateTime(LocalDateTime.now());

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, medicalRecords);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create medical record.");
                }

                return;
            }
        }

        throw new RuntimeException("Regular medical record does not exist.");
    }

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
                        (triageColor == TriageColor.BLACK) ? RecordStatus.COMPLETED : RecordStatus.PENDING
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
            if (emergencyCaseId.equals(medicalRecord.getEmergencyCaseId())) {

                medicalRecord.setHandledBy(handledBy);
                medicalRecord.setDiagnosis(diagnosis);
                medicalRecord.setTreatmentGiven(treatmentGiven);
                medicalRecord.setPrescription(prescription);
                medicalRecord.setNotes(notes);
                medicalRecord.setRecordDateTime(LocalDateTime.now());
                medicalRecord.setRecordStatus(RecordStatus.COMPLETED);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, medicalRecords);
                } catch (IOException e) {
                    throw new RuntimeException("Unable to update medical record.");
                }

                return;
            }
        }

        throw new RuntimeException("Emergency medical record does not exist.");
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

    public int getCompletedEmergencyCasesTodayCount(TriageColor triagecolor) {

        return (int) getMedicalRecordsInternal()
                .stream()
                .filter(record -> record.getRecordType() == RecordType.EMERGENCY)
                .filter(record -> record.getTriageColor() == triagecolor)
                .filter(record -> record.getRecordStatus() == RecordStatus.COMPLETED)
                .filter(record -> LocalDateTime.parse(record.getRecordDateTime()).toLocalDate().equals(LocalDate.now()))
                .count();
    }

    public void resheduleMedicalRecord(String appointmentId, String doctorName) {
        ArrayList<MedicalRecord> records = getMedicalRecordsInternal();

        for (MedicalRecord record : records) {
            if (appointmentId.equals(record.getAppointmentId())) {
                record.setHandledBy(doctorName);

                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(medicalRecordsFile, records);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to update medical record.");
                }
            }
        }
    }

    public int getRegularPendingRecordsCount(String doctorName) {
        return (int) getMedicalRecordsInternal()
                .stream()
                .filter(record -> record.getRecordType() == RecordType.REGULAR)
                .filter(record -> doctorName.equals(record.getHandledBy()))
                .filter(record -> record.getRecordStatus() == RecordStatus.PENDING)
                .count();
    }

    public List<MedicalRecordDTO> getPatientHistory(String patientId) {
        return getMedicalRecords()
            .stream()
            .filter(record -> patientId.equals(record.getPatientId()))
            .toList();
    }
}