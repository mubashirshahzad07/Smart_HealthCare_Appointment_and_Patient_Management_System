 package patient.management.system.dao;

 import patient.management.system.dto.AppointmentDTO;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import patient.management.system.dto.DoctorAvailabilityDTO;
 import patient.management.system.dto.DoctorDTO;
 import patient.management.system.model.Appointment;
 import patient.management.system.model.DoctorSchedule;

 import java.io.File;
 import java.io.IOException;
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;

 public class AppointmentDAO {
     private final ObjectMapper mapper = new ObjectMapper();
     private final File file = new File("data/appointments.json");

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
         updateAppointmentStatus();

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
     private void updateAppointmentStatus() {
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
         updateAppointmentStatus();

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

     /**
      * reschedule the target appointment and following appointments willing to reschedule
      */
     public void rescheduleAppointment(
             String appointmentId, int appointmentYear, int appointmentMonth,
             int appointmentDay, int appointmentHour, String doctorId,
             String receptionistId, boolean willingToReschedule
     ) {

         ArrayList<Appointment> appointments = getAppointmentsInternal();
         Appointment targetAppointment = null;

         for (Appointment appointment : appointments) {

             if (appointment.getAppointmentId().equals(appointmentId)) {
                 targetAppointment = appointment;
                 break;
             }
         }

         if (targetAppointment == null) {
             throw new RuntimeException("Appointment not found.");
         }

         boolean reschedulable =
                 targetAppointment.getStatus().equals(Appointment.Status.SCHEDULED.toString())
                 || targetAppointment.getStatus().equals(Appointment.Status.RESCHEDULED.toString());

         if (!reschedulable) {
             throw new RuntimeException("Appointment cannot be rescheduled.");
         }

         String vacantDate = targetAppointment.getAppointmentDate();
         int vacantHour = targetAppointment.getAppointmentHour();

         slotAvailable(
                 appointments,
                 String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay),
                 appointmentHour, doctorId, Appointment.Type.REGULAR
         );

         targetAppointment.setAppointmentDate(String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay));
         targetAppointment.setAppointmentHour(appointmentHour);
         targetAppointment.setStatus(Appointment.Status.RESCHEDULED);
         targetAppointment.setWillingToReschedule(willingToReschedule);
         targetAppointment.setReceptionistId(receptionistId);

         rescheduleFollowingAppointments(appointments, vacantDate, vacantHour, doctorId);

         try {
             mapper.writerWithDefaultPrettyPrinter().writeValue(file, appointments);
         } catch (IOException e) {
             throw new RuntimeException("Unable to reschedule appointment.");
         }
     }

     private void rescheduleFollowingAppointments(ArrayList<Appointment> appointments, String vacantDate, int vacantHour, String doctorId) {

         while (true) {
             Appointment closestAppointment = null;
             LocalDateTime closestTime = null;
             LocalDateTime vacantDateTime = LocalDate.parse(vacantDate).atTime(vacantHour, 0);

             for (Appointment appointment : appointments) {

                 boolean sameDoctor = appointment.getDoctorId().equals(doctorId);
                 boolean isWilling = appointment.getWillingToReschedule();

                 boolean validStatus =
                         appointment.getStatus().equals(Appointment.Status.SCHEDULED.toString())
                         || appointment.getStatus().equals(Appointment.Status.RESCHEDULED.toString());

                 LocalDateTime appointmentDateTime =
                         LocalDate.parse(appointment.getAppointmentDate()).
                         atTime(appointment.getAppointmentHour(), 0);

                 boolean afterVacancy = appointmentDateTime.isAfter(vacantDateTime);

                 if (sameDoctor && isWilling && validStatus && afterVacancy) {

                     if (closestAppointment == null || appointmentDateTime.isBefore(closestTime)) {

                         closestAppointment = appointment;
                         closestTime = appointmentDateTime;
                     }
                 }
             }

             if (closestAppointment == null) {
                 break;
             }

             String nextVacantDate = closestAppointment.getAppointmentDate();
             int nextVacantHour = closestAppointment.getAppointmentHour();

             closestAppointment.setAppointmentDate(vacantDate);
             closestAppointment.setAppointmentHour(vacantHour);
             closestAppointment.setStatus(Appointment.Status.RESCHEDULED);

             vacantDate = nextVacantDate;
             vacantHour = nextVacantHour;
         }
     }

     public List<DoctorAvailabilityDTO> getAvailableDoctors(String appointmentDate) {

         LocalDate parsedDate = LocalDate.parse(appointmentDate);
         DoctorSchedule.Day day = DoctorSchedule.Day.valueOf(parsedDate.getDayOfWeek().name());

         DoctorDAO doctorDAO = new DoctorDAO();
         DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAO();

         List<DoctorDTO> doctors = doctorDAO.getDoctors();
         List<DoctorSchedule> schedules = scheduleDAO.getSchedules();
         ArrayList<Appointment> appointments = getAppointmentsInternal();

         ArrayList<DoctorAvailabilityDTO> result = new ArrayList<>();

         for (DoctorDTO doctor : doctors) {

             ArrayList<Integer> freeSlots =
                     new ArrayList<>();

             for (DoctorSchedule schedule : schedules) {

                 boolean sameDoctor = schedule.getDoctorId().equals(doctor.getDoctorId());
                 boolean sameDay = (schedule.getDay() == day);

                 if (sameDoctor && sameDay) {

                     if (schedule.getShift() == DoctorSchedule.Shift.MORNING) {

                         for (int hour = 9; hour < 15; hour++) {
                             freeSlots.add(hour);
                         }
                     } else if (schedule.getShift() == DoctorSchedule.Shift.EVENING) {

                         for (int hour = 15; hour < 21; hour++) {

                             freeSlots.add(hour);
                         }
                     }
                 }
             }

             for (Appointment appointment : appointments) {

                 boolean appointmentMatches =
                         appointment.getDoctorId().equals(doctor.getDoctorId())
                         && appointment.getAppointmentDate().equals(appointmentDate);

                 if (appointmentMatches) {

                     freeSlots.remove(appointment.getAppointmentHour());
                 }
             }

             result.add(
                     new DoctorAvailabilityDTO(
                             doctor.getDoctorId(),
                             doctor.getName(),
                             doctor.getSpecialization(),
                             doctor.getAppointmentFee(),
                             freeSlots
                     )
             );
         }

         return result;
     }
 }