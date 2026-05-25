package patient.management.system.service;

import patient.management.system.dao.AppointmentDAO;
import patient.management.system.dto.AppointmentDTO;
import patient.management.system.model.Appointment;

import java.util.List;

public class AppointmentService {
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void addAppointment(
            int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
            String patientId, String doctorId, String receptionistId, String patientDescription,
            Appointment.Status status, Appointment.Type type, boolean willingToReschedule
    ) {

        try {

            appointmentDAO.addAppointment(
                    appointmentYear, appointmentMonth, appointmentDay, appointmentHour, patientId, doctorId,
                    receptionistId, patientDescription, status, type, willingToReschedule
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateAppointmentStatus() {
        try {
            appointmentDAO.updateAppointmentStatus();
        } catch (RuntimeException e) {
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
}
