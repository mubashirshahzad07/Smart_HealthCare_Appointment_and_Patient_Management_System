package patient.management.system.service;

import patient.management.system.dao.*;
import patient.management.system.dto.*;
import patient.management.system.model.*;

import java.util.List;

public class ReceptionistService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final EmergencyCaseDAO emergencyCaseDAO = new EmergencyCaseDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public void addAppointment(
            int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
            String patientId, String doctorId, String receptionistId, String patientDescription,
            Appointment.Status status, boolean willingToReschedule, String patientName, String doctorName
    ) {

        try {

            appointmentDAO.addAppointment(
                    appointmentYear, appointmentMonth, appointmentDay, appointmentHour, patientId, doctorId,
                    receptionistId, patientDescription, status, willingToReschedule, patientName, doctorName
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<AppointmentDTO> getAppointments() {
        try {
            return appointmentDAO.getAppointments();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void cancelAppointment(String appointmentId, String doctorId) {
        try {
            appointmentDAO.cancelAppointment(appointmentId, doctorId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void rescheduleAppointment(
            String appointmentId, int appointmentYear, int appointmentMonth,
            int appointmentDay, int appointmentHour, 
            String doctorId, boolean willingToReschedule,
            String patientDescription, String doctorName
    ) {

        try {
            appointmentDAO.rescheduleAppointment(
                    appointmentId, appointmentYear, appointmentMonth,
                    appointmentDay, appointmentHour, doctorId,
                    willingToReschedule, patientDescription, doctorName
            );
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void addPatient(String name, String gender, int age, String email, String cnic) {
        try {
            patientDAO.addPatient(name, gender, age, email, cnic);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<PatientDTO> getPatients() {
        try {
            return patientDAO.getPatients();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void registerEmergencyCase(
            String patientId,
            boolean isTemporaryPatient,
            String patientName,
            int age,
            String gender,
            String cnic,
            String initialComplaint,
            TriageColor triageColor,
            String triageRemark) {

        try {
            emergencyCaseDAO.addEmergencyCase(
                    patientId,
                    isTemporaryPatient,
                    patientName,
                    age,
                    gender,
                    cnic,
                    initialComplaint,
                    triageColor,
                    triageRemark);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<EmergencyCaseDTO> getEmergencyCases(String triageColor) {
        try {
            return emergencyCaseDAO.getCasesByTriageColor(triageColor);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param emergencyId temp id or emergency case id
     */
    public void linkTemporaryPatient(String emergencyId, String patientId) {
        try {
            emergencyCaseDAO.linkTemporaryPatient(emergencyId, patientId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param emergencyId temp id or emergency case id
     */
    public void registerAndLinkTemporaryPatient(String emergencyId, String patientName, int age, String gender, String phoneNumber, String cnic, String email) {
        try {
            emergencyCaseDAO.registerAndLinkTemporaryPatient(emergencyId, patientName, age, gender, phoneNumber, cnic, email);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<String> getDoctorAvailableSlotsThisWeek(String doctorId) {
        try {
            return appointmentDAO.getDoctorAvailableSlotsThisWeek(doctorId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorDTO> getActiveDoctors() {
        try {
            return doctorDAO.getActiveDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<PatientDTO> searchPatient(String query) {
        return patientDAO.searchPatient(query);
    }

    public List<AppointmentDTO> searchAppointments(String query) {
        return appointmentDAO.searchAppointments(query);
    }

    public List<EmergencyCaseDTO> getTemporaryPatients() {
        return emergencyCaseDAO.getTemporaryPatients();
    }

    public List<EmergencyCaseDTO> searchTemporaryPatients(String query) {
        return emergencyCaseDAO.searchTemporaryPatients(query);
    }
}