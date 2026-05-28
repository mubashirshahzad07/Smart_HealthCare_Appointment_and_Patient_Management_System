package patient.management.system.service;

import patient.management.system.dao.*;
import patient.management.system.dto.*;
import patient.management.system.model.*;

import java.util.List;

public class ReceptionistService {
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void addAppointment(
            int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
            String patientId, String doctorId, String receptionistId, String patientDescription,
            Appointment.Status status, boolean willingToReschedule
    ) {

        try {

            appointmentDAO.addAppointment(
                    appointmentYear, appointmentMonth, appointmentDay, appointmentHour, patientId, doctorId,
                    receptionistId, patientDescription, status, willingToReschedule
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
            int appointmentDay, int appointmentHour, String doctorId,
            String receptionistId, boolean willingToReschedule
    ) {

        try {
            appointmentDAO.rescheduleAppointment(
                    appointmentId, appointmentYear, appointmentMonth,
                    appointmentDay, appointmentHour, doctorId,
                    receptionistId, willingToReschedule
            );
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorAvailabilityDTO> getAvailableDoctors(String appointmentDate) {
        try {
            return appointmentDAO.getAvailableDoctors(appointmentDate);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
