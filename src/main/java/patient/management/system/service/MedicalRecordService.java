package patient.management.system.service;

import patient.management.system.dao.MedicalRecordDAO;
import patient.management.system.dto.MedicalRecordDTO;

import java.util.List;

public class MedicalRecordService {
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    public List<MedicalRecordDTO> getPatientHistory(String patientId) {
        return medicalRecordDAO.getPatientMedicalHistory(patientId);
    }

    public List<MedicalRecordDTO> getMedicalRecords() {
        return medicalRecordDAO.getMedicalRecords();
    }
}