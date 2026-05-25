 package patient.management.system.dao;

 import patient.management.system.dto.AppointmentDTO;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import patient.management.system.model.Appointment;

 import java.io.File;
 import java.io.IOException;
 import java.time.LocalDate;
 import java.time.LocalTime;
 import java.util.ArrayList;
 import java.util.List;

 // add validation so that emergency appointment is available everytime regardless of the appointment hour
 public class AppointmentDAO {
     private final ObjectMapper mapper = new ObjectMapper();
     private final File file = new File("data/appointments.json");

     public static void main(String[] args) {
         new AppointmentDAO().addAppointment(2026, 5, 25, 20, "PAT-001",
                 "DOCT-0001", "RECP-0001", "ILL", Appointment.Status.SCHEDULED, Appointment.Type.REGULAR, true);
         new AppointmentDAO().updateAppointments();
     }


     public void addAppointment(int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
                                String patientId, String doctorId, String receptionistId, String patientDescription,
                                Appointment.Status status, Appointment.Type type, boolean willingToReschedule
                                ) {
         Appointment newAppointment = new Appointment(appointmentYear, appointmentMonth, appointmentDay, appointmentHour,
                                                      patientId, doctorId, receptionistId, patientDescription, status, type, willingToReschedule
                                                     );

         ArrayList<Appointment> appointments = getAppointmentsInternal();
         slotAvailable(appointments, String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay), appointmentHour, doctorId); // throws an exception so handle it in service layer
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
      * checks if the selected slot is available and within working hours
      * @param appointments
      * @param appointmentDate
      * @param appointmentHour
      * @param doctorId
      */
     private void slotAvailable(ArrayList<Appointment> appointments, String appointmentDate, int appointmentHour, String doctorId) {
         int openingHour = 9;
         int closingHour = 21;

         for (Appointment appointment : appointments) {
             if (
                 (appointment.getAppointmentDate().equals(appointmentDate))
                 && (appointmentHour == appointment.getAppointmentHour())
                 && (doctorId.equals(appointment.getDoctorId()))
                 || (appointmentHour < openingHour || appointmentHour > closingHour - 1)
             ) {
                 throw new RuntimeException("Selected slot is not available.");
             }
         }
     }

     public void updateAppointments() {
         ArrayList<Appointment> appointments = new AppointmentDAO().getAppointmentsInternal();
         int currentHour = LocalTime.now().getHour();
         String currentDate = LocalDate.now().toString();

         for (Appointment appointment : appointments) {
             if ((currentHour > appointment.getAppointmentHour()) && (currentDate.equals(appointment.getAppointmentDate()))) {
                 appointment.setStatus(Appointment.Status.COMPLETED);
             } else if ((currentHour == appointment.getAppointmentHour()) && (currentDate.equals(appointment.getAppointmentDate()))) {
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