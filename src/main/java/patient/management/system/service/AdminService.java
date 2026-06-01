package patient.management.system.service;

import patient.management.system.dao.*;
import patient.management.system.dto.*;
import patient.management.system.model.*;

import java.util.List;

public class AdminService {

    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();
    private final EmergencyTeamDAO emergencyTeamDAO = new EmergencyTeamDAO();
    private final EmergencyCaseDAO emergencyCaseDAO = new EmergencyCaseDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    public void addDoctor(
            String username, String password, String doctorName, double appointmentFee,
            Doctor.Specialization specialization,DoctorSchedule.Day startDay,
            DoctorSchedule.Day endDay, DoctorSchedule.Shift shift) {

        try {
            String doctorId = doctorDAO.addDoctor(username, password, doctorName, appointmentFee, specialization);
            doctorScheduleDAO.addSchedule(doctorId, startDay, endDay, shift);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateDoctorSchedule(String doctorId, DoctorSchedule.Day startDay, DoctorSchedule.Day endDay, DoctorSchedule.Shift shift, double fee) {

        try {
            doctorScheduleDAO.updateDoctorSchedule(doctorId, startDay, endDay, shift, fee);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorDTO> getAllDoctors() {
        try {
            return doctorDAO.getAllDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public int getTotalDoctorsCount() {
        return getAllDoctors().size();
    }

    public List<DoctorDTO> getActiveDoctors() {
        try {
            return doctorDAO.getActiveDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorDTO> getInactiveDoctors() {
        try {
            return doctorDAO.getInactiveDoctors();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id doctor ID or user ID
     */
    public void activateDoctor(String id) {
        try {
            doctorDAO.activateDoctor(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id doctor ID or user ID
     */
    public void inactivateDoctor(String id) {
        try {
            doctorDAO.inactivateDoctor(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id receptionist ID or user ID
     */
    public void activateReceptionist(String id) {
        try {
            receptionistDAO.activateReceptionist(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id receptionist ID or user ID
     */
    public void inactivateReceptionist(String id) {
        try {
            receptionistDAO.inactivateReceptionist(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<ReceptionistDTO> getAllReceptionists() {
        try {
            return receptionistDAO.getAllReceptionists();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public int getTotalReceptionistCount() {
        return getAllReceptionists().size();
    }

    public List<ReceptionistDTO> getActiveReceptionists() {
        try {
            return receptionistDAO.getActiveReceptionists();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<ReceptionistDTO> getInactiveReceptionists() {
        try {
            return receptionistDAO.getInactiveReceptionists();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<AppointmentDTO> getCancelledAppointments() {
        try {
            return appointmentDAO.getCancelledAppointments();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<AppointmentDTO> getRescheduledAppointments() {
        try {
            return appointmentDAO.getRescheduledAppointments();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public int getTotalBookedAppointmentsCount() {
        return appointmentDAO.getAppointments().size() + getTotalCancelledAppointmentsCount();
    }

    public int getTotalCancelledAppointmentsCount() {
        return getCancelledAppointments().size();
    }

    public int getTotalRescheduledAppointmentsCount() {
        return getRescheduledAppointments().size();
    }

    public double getNetAmount() {
        try {
            return appointmentDAO.getNetFeesCollected();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public double getTotalFeesCollected() {
        try {
            return appointmentDAO.getTotalFeesCollected();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public int getTotalRefundsCount() {
        try {
            return appointmentDAO.getTotalRefundsCount();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void addEmergencyTeam(String username, String name, String password, TriageColor triageColor) {
        try {
            emergencyTeamDAO.addEmergencyTeam(username, name, password, triageColor);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<EmergencyTeamDTO> getEmergencyTeams() {
        try {
            return emergencyTeamDAO.getEmergencyTeams();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public int getTotalEmergencyTeamsCount() {
        return getEmergencyTeams().size();
    }

    public int getTodayAppointmentsCount() {
        return appointmentDAO.getTodayAppointmentsCount();
    }

    public int getUpcomingAppointmentsCount() {
        return appointmentDAO.getUpcomingAppointmentsCount();
    }

    public int getTotalEmergencyCasesCount() {
        return emergencyCaseDAO.getEmergencyCases().size();
    }

    public int getTemporaryLinksCount() {
        return emergencyCaseDAO.getTemporaryLinksCount();
    }

    public int getTotalPatientsCount() {
        return patientDAO.getPatients().size();
    }

    public List<AppointmentReportDTO> getAppointmentReports() {
        try {
            return appointmentDAO.getAppointmentReports();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<EmergencyReportDTO> getEmergencyReport() {
        try {
            return emergencyCaseDAO.getEmergencyReport();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}