package patient.management.system.dao;

import patient.management.system.dto.AppointmentDTO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import patient.management.system.dto.DoctorAvailabilityDTO;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Appointment;
import patient.management.system.model.DoctorSchedule;

import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final File appointmentsFile = new File("data/appointments.json");
    private final File cancelledAppointmentsFile = new File("data/cancelled_appointments.json");
    private final File rescheduledAppointmentsFile = new File("data/rescheduled_appointments.json");
    private final String totalFeesFilePath = "data/total_fees.json";

    public void addAppointment(
            int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
            String patientId, String doctorId, String receptionistId, String patientDescription,
            Appointment.Status status, boolean willingToReschedule) {

        PatientDAO patientDAO = new PatientDAO();
        patientDAO.patientRegistered(patientId);

        ArrayList<Appointment> appointments = getAppointmentsInternal();
        slotAvailable(
            appointments, 
            String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay),
            appointmentHour, doctorId
        ); // throws an exception so handle it in service layer

        Appointment newAppointment = 
                                new Appointment(
                                    appointmentYear, appointmentMonth, 
                                    appointmentDay, appointmentHour,
                                    patientId, doctorId, receptionistId, 
                                    patientDescription, status, willingToReschedule
                                );

        appointments.add(newAppointment);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(appointmentsFile, appointments);
        } catch (IOException e) {
            throw new RuntimeException("Unable to register appointment.");
        }

        setTotalFeesCollected(doctorDAO.getDoctorAppointmentFee(doctorId));
    }

    private void setTotalFeesCollected(double appiontmentFee) {
        double newTotalFeeCollected = getTotalFeesCollected() + appiontmentFee;

        Path path = Path.of(totalFeesFilePath);
        try {
            if (!Files.exists(path) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
            }

            Files.writeString(path, String.format("%f", newTotalFeeCollected));
        } catch(IOException e) {
            throw new RuntimeException("Failed to update total fee collected.");
        }
    }

    public double getTotalFeesCollected() {
        Path path = Path.of(totalFeesFilePath);

        try {
            if ((!Files.exists(path)) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "0");
            }

            String totalFeeRead = Files.readString(path).trim();

            if (totalFeeRead.isEmpty()) {
                totalFeeRead = "0";
            }

            
            double totalFeeCollected = Double.parseDouble(totalFeeRead);
            Files.writeString(path, String.format("%f", totalFeeRead));

            return totalFeeCollected;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load total fee collected.");
        }

    }

    private ArrayList<Appointment> getAppointmentsInternal() {
        updateAppointmentStatus();

        try {
            if (!appointmentsFile.exists() || appointmentsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    appointmentsFile,
                    new TypeReference<ArrayList<Appointment>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load appointments data.");
        }
    }

    /**
     * checks if the selected slot is available and within working hours
     */
    private void slotAvailable(ArrayList<Appointment> appointments, String appointmentDate, int appointmentHour,
            String doctorId) {
        final int OPENING_HOUR = 9;
        final int CLOSING_HOUR = 21;

        boolean appointmentHourOutOfRange = (appointmentHour < OPENING_HOUR || appointmentHour >= CLOSING_HOUR);

        if (appointmentHourOutOfRange) {
            throw new RuntimeException("Unable to book slot.\nHospital timing: " + OPENING_HOUR + " - " + CLOSING_HOUR);
        }

        for (Appointment appointment : appointments) {
            boolean slotOccupied = (appointment.getAppointmentDate().equals(appointmentDate))
                    && (appointmentHour == appointment.getAppointmentHour())
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

            LocalDateTime appointmentStart = LocalDate.parse(appointment.getAppointmentDate())
                    .atTime(appointment.getAppointmentHour(), 0);
            LocalDateTime appointmentEnd = appointmentStart.plusHours(1);

            boolean isCompleted = !CURRENT_DATE_TIME.isBefore(appointmentEnd);
            boolean isInProgress = !CURRENT_DATE_TIME.isBefore(appointmentStart)
                    && CURRENT_DATE_TIME.isBefore(appointmentEnd);

            if (isCompleted) {
                appointment.setStatus(Appointment.Status.COMPLETED);
            } else if (isInProgress) {
                appointment.setStatus(Appointment.Status.IN_PROGRESS);
            }
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(appointmentsFile, appointments);
        } catch (IOException e) {
            throw new RuntimeException("Unable to update appointments.");
        }
    }

    public List<AppointmentDTO> getAppointments() {
        updateAppointmentStatus();

        try {
            if (!appointmentsFile.exists() || appointmentsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    appointmentsFile,
                    new TypeReference<List<AppointmentDTO>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load appointments data.");
        }
    }

    private ArrayList<Appointment> getRescheduledAppointmentsInternal() {

        try {
            if (!rescheduledAppointmentsFile.exists() || rescheduledAppointmentsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    rescheduledAppointmentsFile,
                    new TypeReference<ArrayList<Appointment>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load rescheduled appointments data.");
        }
           
    }

    public List<AppointmentDTO> getRescheduledAppointments() {

        try {
            if (!rescheduledAppointmentsFile.exists() || rescheduledAppointmentsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    rescheduledAppointmentsFile,
                    new TypeReference<List<AppointmentDTO>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load rescheduled appointments data.");
        }
           
    }


    private ArrayList<Appointment> getCancelledAppointmentsInternal() {

        try {
            if (!cancelledAppointmentsFile.exists() || cancelledAppointmentsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    cancelledAppointmentsFile,
                    new TypeReference<ArrayList<Appointment>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load cancelled appointments data.");
        }
           
    }

    public List<AppointmentDTO> getCancelledAppointments() {

        try {
            if (!cancelledAppointmentsFile.exists() || cancelledAppointmentsFile.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    cancelledAppointmentsFile,
                    new TypeReference<List<AppointmentDTO>>() {
                    });

        } catch (IOException e) {
            throw new RuntimeException("Unable to load cancelled appointments data.");
        }
           
    }

    /**
     * cancel the target appointment and following appointments willing to
     * reschedule are rescheduled
     */
    public void cancelAppointment(String appointmentId, String doctorId) {

        ArrayList<Appointment> appointments = getAppointmentsInternal();
        ArrayList<Appointment> cancelledAppointments = getCancelledAppointmentsInternal();
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

        boolean cancellable = targetAppointment.getStatus().equals(Appointment.Status.SCHEDULED.toString())
                || targetAppointment.getStatus().equals(Appointment.Status.RESCHEDULED.toString());

        if (!cancellable) {
            throw new RuntimeException("Appointment cannot be cancelled.");
        }

        String vacantDate = targetAppointment.getAppointmentDate();
        int vacantHour = targetAppointment.getAppointmentHour();

        cancelledAppointments.add(targetAppointment);
        appointments.remove(targetAppointment);

        rescheduleFollowingAppointments(appointments, vacantDate, vacantHour, doctorId);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(appointmentsFile, appointments);
            mapper.writerWithDefaultPrettyPrinter().writeValue(cancelledAppointmentsFile, cancelledAppointments);
        } catch (IOException e) {
            throw new RuntimeException("Unable to cancel appointment.");
        }
    }

    /**
     * reschedule the target appointment and following appointments willing to
     * reschedule are rescheduled
     */
    public void rescheduleAppointment(
            String appointmentId, int appointmentYear, int appointmentMonth,
            int appointmentDay, int appointmentHour, String doctorId,
            String receptionistId, boolean willingToReschedule) {

        ArrayList<Appointment> appointments = getAppointmentsInternal();
        ArrayList<Appointment> rescheduledAppointments = getRescheduledAppointmentsInternal();

        Appointment targetAppointment = null;

        for (Appointment appointment : appointments) {

            if (appointment.getAppointmentId().equals(appointmentId)) {
                targetAppointment = appointment;
                rescheduledAppointments.add(targetAppointment);
                break;
            }
        }

        if (targetAppointment == null) {
            throw new RuntimeException("Appointment not found.");
        }

        boolean reschedulable = targetAppointment.getStatus().equals(Appointment.Status.SCHEDULED.toString())
                || targetAppointment.getStatus().equals(Appointment.Status.RESCHEDULED.toString());

        if (!reschedulable) {
            throw new RuntimeException("Appointment cannot be rescheduled.");
        }

        String vacantDate = targetAppointment.getAppointmentDate();
        int vacantHour = targetAppointment.getAppointmentHour();

        slotAvailable(
                appointments,
                String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay),
                appointmentHour, doctorId);

        targetAppointment.setAppointmentDate(String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay));
        targetAppointment.setAppointmentHour(appointmentHour);
        targetAppointment.setStatus(Appointment.Status.RESCHEDULED);
        targetAppointment.setWillingToReschedule(willingToReschedule);
        targetAppointment.setReceptionistId(receptionistId);

        rescheduleFollowingAppointments(appointments, vacantDate, vacantHour, doctorId);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(appointmentsFile, appointments);
            mapper.writerWithDefaultPrettyPrinter().writeValue(rescheduledAppointmentsFile, rescheduledAppointments);
        } catch (IOException e) {
            throw new RuntimeException("Unable to reschedule appointment.");
        }
    }

    private void rescheduleFollowingAppointments(ArrayList<Appointment> appointments, String vacantDate, int vacantHour,
            String doctorId) {

        while (true) {
            Appointment closestAppointment = null;
            LocalDateTime closestTime = null;
            LocalDateTime vacantDateTime = LocalDate.parse(vacantDate).atTime(vacantHour, 0);

            for (Appointment appointment : appointments) {

                boolean sameDoctor = appointment.getDoctorId().equals(doctorId);
                boolean isWilling = appointment.getWillingToReschedule();

                boolean validStatus = appointment.getStatus().equals(Appointment.Status.SCHEDULED.toString())
                        || appointment.getStatus().equals(Appointment.Status.RESCHEDULED.toString());

                LocalDateTime appointmentDateTime = LocalDate.parse(appointment.getAppointmentDate())
                        .atTime(appointment.getAppointmentHour(), 0);

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

        List<DoctorDTO> doctors = doctorDAO.getActiveDoctors();
        List<DoctorSchedule> schedules = scheduleDAO.getSchedules();
        ArrayList<Appointment> appointments = getAppointmentsInternal();

        ArrayList<DoctorAvailabilityDTO> result = new ArrayList<>();

        for (DoctorDTO doctor : doctors) {

            ArrayList<Integer> freeSlots = new ArrayList<>();

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

                boolean appointmentMatches = appointment.getDoctorId().equals(doctor.getDoctorId())
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
                            freeSlots));
        }

        return result;
    }
}