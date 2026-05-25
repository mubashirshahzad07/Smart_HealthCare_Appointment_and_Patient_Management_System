 package patient.management.system.dao;

 import patient.management.system.dto.AppointmentDTO;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import patient.management.system.model.Appointment;

 import java.io.File;
 import java.io.IOException;
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;

 public class AppointmentDAO {
     private final ObjectMapper mapper = new ObjectMapper();
     private final File file = new File("data/appointments.json");

     public static void main(String[] args) {
//         new AppointmentDAO().addAppointment(2026, 5, 25, 19, "PAT-0002",
//                 "DOCT-0001", "RECP-0001", "ILL", Appointment.Status.SCHEDULED, Appointment.Type.REGULAR, true);
     }


     public void addAppointment(
             int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
             String patientId, String doctorId, String receptionistId, String patientDescription,
             Appointment.Status status, Appointment.Type type, boolean willingToReschedule
     ) {

         PatientDAO patientDAO = new PatientDAO();
         patientDAO.patientRegistered(patientId);

         ArrayList<Appointment> appointments = getAppointmentsInternal();
         slotAvailable(appointments, String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay), appointmentHour, doctorId, type); // throws an exception so handle it in service layer

         Appointment newAppointment = new Appointment(appointmentYear, appointmentMonth, appointmentDay, appointmentHour,
                 patientId, doctorId, receptionistId, patientDescription, status, type, willingToReschedule
         );
         appointments.add(newAppointment);

         try {
             mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);

         } catch (IOException e) {
             throw new RuntimeException("Unable to register appointment.");
         }
     }

     private ArrayList<Appointment> getAppointmentsInternal() {

         try {
             if (!file.exists() || file.length() == 0) {
                 return new ArrayList<>();
             }

             return mapper.readValue(
                     file,
                     new TypeReference<ArrayList<Appointment>>(){}
             );

         } catch (IOException e) {
             throw new RuntimeException("Unable to load appointments data.");
         }
     }

     /**
      * checks if the selected slot is available and within working hours (for regular appointments)
      */
     private void slotAvailable(ArrayList<Appointment> appointments, String appointmentDate, int appointmentHour, String doctorId, Appointment.Type type) {
         final int OPENING_HOUR = 9;
         final int CLOSING_HOUR = 21;

         boolean appointmentHourOutOfRange = (appointmentHour < OPENING_HOUR || appointmentHour >= CLOSING_HOUR) && (type == Appointment.Type.REGULAR);

         if (appointmentHourOutOfRange) {
             throw new RuntimeException("Unable to book slot.\nHospital timing: " + OPENING_HOUR + " - " + CLOSING_HOUR);
         }

         for (Appointment appointment : appointments) {
             boolean slotOccupied =
                     (appointment.getAppointmentDate().equals(appointmentDate))
                     && (appointmentHour == appointment.getAppointmentHour())
                     && (appointment.getType().equals(type.toString()))
                     && (doctorId.equals(appointment.getDoctorId()));

             if (slotOccupied) {
                 throw new RuntimeException("Selected slot is not available.");
             }
         }
     }


     /**
      * updates the status of appointments
      */
     public void updateAppointmentStatus() {
         ArrayList<Appointment> appointments = new AppointmentDAO().getAppointmentsInternal();
         final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.now();

         for (Appointment appointment : appointments) {

             LocalDateTime appointmentStart = LocalDate.parse(appointment.getAppointmentDate()).atTime(appointment.getAppointmentHour(), 0);
             LocalDateTime appointmentEnd = appointmentStart.plusHours(1);

            boolean isCompleted = !CURRENT_DATE_TIME.isBefore(appointmentEnd);
             boolean isInProgress = !CURRENT_DATE_TIME.isBefore(appointmentStart) && CURRENT_DATE_TIME.isBefore(appointmentEnd);

             if (isCompleted) {
                 appointment.setStatus(Appointment.Status.COMPLETED);
             } else if (isInProgress) {
                 appointment.setStatus(Appointment.Status.IN_PROGRESS);
             }
         }

         try {
             mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
         } catch (IOException e) {
             throw new RuntimeException("Unable to update appointments.");
         }
     }

     public List<AppointmentDTO> getAppointments() {

         try {
             if (!file.exists() || file.length() == 0) {
                 return new ArrayList<>();
             }

             return mapper.readValue(
                     file,
                     new TypeReference<List<AppointmentDTO>>(){}
             );

         } catch (IOException e) {
             throw new RuntimeException("Unable to load appointments data.");
         }
     }
 }