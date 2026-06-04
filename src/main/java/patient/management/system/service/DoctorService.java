package patient.management.system.service;

import patient.management.system.dao.*;
import patient.management.system.dto.*;

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

    public DoctorDTO getDoctorByUserId(String userId) {
        try {
            return new DoctorDAO().getDoctorByUserId(userId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<AppointmentDTO> getAppointmentsForDoctor(String doctorId) {
        try {
            return appointmentDAO.getAppointmentsForDoctor(doctorId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<AppointmentDTO> getPendingMedicalRecordAppointments(String doctorId) {
        if (doctorId == null || doctorId.isEmpty()) {
            throw new RuntimeException("Doctor ID is required.");
        }

        return appointmentDAO.getPendingMedicalRecordAppointments(doctorId);
    }

    public List<MedicalRecordDTO> getPatientHistoryForDoctor(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            throw new RuntimeException("Patient ID is required.");
        }

        return medicalRecordDAO.getPatientHistory(patientId);
    }
}
