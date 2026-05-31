package patient.management.system.service;

import patient.management.system.dao.AppointmentDAO;
import patient.management.system.dao.MedicalRecordDAO;
import patient.management.system.dto.MedicalRecordDTO;

import java.util.List;

public class DoctorService {
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void updateMedicalRecord(
        String appointmentId,
        String diagnosis,
        String treatmentGiven,
        String prescription,
        String notes
    ) {

        try {
            medicalRecordDAO.updateRegularMedicalRecord(appointmentId, diagnosis, treatmentGiven, prescription, notes);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<MedicalRecordDTO> getPatientHistory(String patientId) {
        return medicalRecordDAO.getPatientHistory(patientId);
    }

    public int getTodayAppointmentsCount(String doctorName) {
        return appointmentDAO.getAppointmentsTodayCount(doctorName);
    }

    public int getUpcomingAppointmentCount(String doctorName) {
        return appointmentDAO.getUpcomingAppointmentsCount(doctorName);
    }

    public int getCompletedTodayCount(String doctorName) {
        return appointmentDAO.getCompletedTodayCount(doctorName);
    }

    public int getPendingRecordsCount(String doctorName) {
        return medicalRecordDAO.getRegularPendingRecordsCount(doctorName);
    }
}