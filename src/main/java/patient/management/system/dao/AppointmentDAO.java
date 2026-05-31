package patient.management.system.dao;

import patient.management.system.dto.AppointmentDTO;
import patient.management.system.dto.DoctorAvailabilityDTO;
import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.Appointment;
import patient.management.system.model.DoctorSchedule;
import patient.management.system.dto.AppointmentReportDTO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAO();
    private final File appointmentsFile = new File("data/appointments.json");
    private final File cancelledAppointmentsFile = new File("data/cancelled_appointments.json");
    private final File rescheduledAppointmentsFile = new File("data/rescheduled_appointments.json");
    private final String netFeesFilePath = "data/net_fees.json";
    private final String totalFeesFilePath = "data/total_fees.json";
    private final String totalRefundsFilePath = "data/total_refunds.json";

    public void addAppointment(
        int appointmentYear, int appointmentMonth, int appointmentDay, int appointmentHour,
        String patientId, String doctorId, String receptionistId, String patientDescription,
        Appointment.Status status, boolean willingToReschedule, String patientName, String doctorName) {

        patientDAO.patientRegistered(patientId);

        ArrayList<Appointment> appointments = getAppointmentsInternal();
        slotAvailable(
            appointments,
            String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay),
            appointmentHour, doctorId);

        Appointment newAppointment = new Appointment(
            appointmentYear, appointmentMonth,
            appointmentDay, appointmentHour,
            patientId, doctorId, receptionistId,
            patientDescription, status, willingToReschedule, doctorName, patientName);

        appointments.add(newAppointment);
        medicalRecordDAO.addRegularMedicalRecord(
            newAppointment.getAppointmentId(), 
            patientId,
            newAppointment.getDoctorName()
        );

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(appointmentsFile, appointments);
        } catch (IOException e) {
            throw new RuntimeException("Unable to register appointment.");
        }

        setNetAmountCollected(doctorDAO.getDoctorAppointmentFee(doctorId));
        setTotalFeesCollected(doctorDAO.getDoctorAppointmentFee(doctorId));
    }

    private void setNetAmountCollected(double appointmentFee) {
        double newNetFeeCollected = getNetFeesCollected() + appointmentFee;

        Path path = Path.of(netFeesFilePath);
        try {
            if (!Files.exists(path) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
            }

            Files.writeString(path, String.format("%f", newNetFeeCollected));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update net fee collected.");
        }
    }

    public double getNetFeesCollected() {
        Path path = Path.of(netFeesFilePath);

        try {
            if ((!Files.exists(path)) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "0");
            }

            String netFee = Files.readString(path).trim();

            if (netFee.isEmpty()) {
                netFee = "0";
            }

            return Double.parseDouble(netFee);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load net fee collected.");
        }

    }

    public void setTotalFeesCollected(double appointmentFee) {
        double newTotalFeeCollected = getTotalFeesCollected() + appointmentFee;

        Path path = Path.of(totalFeesFilePath);
        try {
            if (!Files.exists(path) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
            }

            Files.writeString(path, String.format("%f", newTotalFeeCollected));
        } catch (IOException e) {
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

            String totalFee = Files.readString(path).trim();

            if (totalFee.isEmpty()) {
                totalFee = "0";
            }

            return Double.parseDouble(totalFee);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load total fee collected.");
        }

    }

    public void incrementTotalRefundsCount() {
        int newTotalRefunds = getTotalRefundsCount() + 1;

        Path path = Path.of(totalRefundsFilePath);
        try {
            if (!Files.exists(path) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
            }

            Files.writeString(path, String.format("%d", newTotalRefunds));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update total refunds.");
        }
    }

    public int getTotalRefundsCount() {
        Path path = Path.of(totalRefundsFilePath);

        try {
            if ((!Files.exists(path)) && !(path.getParent() == null)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "0");
            }

            String totalRefunds = Files.readString(path).trim();

            if (totalRefunds.isEmpty()) {
                totalRefunds = "0";
            }

            return Integer.parseInt(totalRefunds);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load total refunds.");
        }

    }

    private ArrayList<Appointment> loadAppointments() {
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

    private ArrayList<Appointment> getAppointmentsInternal() {
        updateAppointmentStatus();
        return loadAppointments();
    }

    /**
     * checks if the selected slot is available and within working hours
     */
    private void slotAvailable(ArrayList<Appointment> appointments, String appointmentDate, int appointmentHour, String doctorId) {
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
        ArrayList<Appointment> appointments = loadAppointments();
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
        double refund = doctorDAO.getDoctorAppointmentFee(doctorId) * 0.75;

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

        setNetAmountCollected(-refund);
        incrementTotalRefundsCount();
    }

    /**
     * Reschedule the target appointment and following appointments willing to
     * reschedule are rescheduled
     */
    public void rescheduleAppointment(
        String appointmentId, int appointmentYear, int appointmentMonth,
        int appointmentDay, int appointmentHour, String doctorId,
        boolean willingToReschedule, String patientDescription, String doctorName) {

        ArrayList<Appointment> appointments = getAppointmentsInternal();
        ArrayList<Appointment> rescheduledAppointments = getRescheduledAppointmentsInternal();

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

        boolean reschedulable = targetAppointment.getStatus().equals(Appointment.Status.SCHEDULED.toString())
        || targetAppointment.getStatus().equals(Appointment.Status.RESCHEDULED.toString());

        if (!reschedulable) {
            throw new RuntimeException("Appointment cannot be rescheduled.");
        }

        String vacantDate = targetAppointment.getAppointmentDate();
        int vacantHour = targetAppointment.getAppointmentHour();

        rescheduledAppointments.add(mapper.convertValue(targetAppointment, Appointment.class));
        appointments.remove(targetAppointment);

        slotAvailable(
            appointments,
            String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay),
            appointmentHour, doctorId);

        targetAppointment.setAppointmentDate(String.format("%d-%02d-%02d", appointmentYear, appointmentMonth, appointmentDay));
        targetAppointment.setAppointmentHour(appointmentHour);
        targetAppointment.setStatus(Appointment.Status.RESCHEDULED);
        targetAppointment.setWillingToReschedule(willingToReschedule);
        targetAppointment.setDoctorName(doctorName);
        targetAppointment.setPatientDescription(patientDescription);
        appointments.add(targetAppointment);

        rescheduleFollowingAppointments(appointments, vacantDate, vacantHour, doctorId);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(appointmentsFile, appointments);
            mapper.writerWithDefaultPrettyPrinter().writeValue(rescheduledAppointmentsFile, rescheduledAppointments);
        } catch (IOException e) {
            throw new RuntimeException("Failed to reschedule appointment.");
        }

        medicalRecordDAO.resheduleMedicalRecord(appointmentId, doctorName);
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

                    freeSlots.remove(Integer.valueOf(appointment.getAppointmentHour()));
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

    public ArrayList<String> getDoctorAvailableSlotsThisWeek(String doctorId) {

        ArrayList<Appointment> appointments = getAppointmentsInternal();
        List<DoctorSchedule> schedules = scheduleDAO.getSchedules();
        ArrayList<String> availableSlots = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {

            LocalDate currentDate = today.plusDays(i);
            DoctorSchedule.Day currentDay = DoctorSchedule.Day.valueOf(currentDate.getDayOfWeek().name());
            ArrayList<Integer> freeSlots = new ArrayList<>();

            for (DoctorSchedule schedule : schedules) {

                boolean sameDoctor = schedule.getDoctorId().equals(doctorId);
                boolean sameDay = (schedule.getDay() == currentDay);

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

            String formattedDate = currentDate.toString();

            for (Appointment appointment : appointments) {

                boolean appointmentMatches = 
                appointment.getDoctorId().equals(doctorId)
                && appointment.getAppointmentDate().equals(formattedDate);

                if (appointmentMatches) {
                    freeSlots.remove(Integer.valueOf(appointment.getAppointmentHour()));
                }
            }

            for (Integer hour : freeSlots) {
                availableSlots.add(currentDay + ", " + formattedDate + " - " + String.format("%02d", hour) + ":00");
            }
        }

        return availableSlots;
    }

    public int getTodayAppointmentsCount() {

        String today = LocalDate.now().toString();

        return (int) getAppointmentsInternal()
        .stream()
        .filter(appointment -> appointment.getAppointmentDate().equals(today))
        .count();
    }

    public int getUpcomingAppointmentsCount() {

        LocalDateTime currentDateTime = LocalDateTime.now();

        return (int) getAppointmentsInternal()
        .stream()
        .filter(appointment -> LocalDate.parse(appointment.getAppointmentDate())
            .atTime(appointment.getAppointmentHour(), 0)
            .isAfter(currentDateTime))
        .count();
    }

    public List<AppointmentDTO> searchAppointments(String query) {
        List<Appointment> appointments = getAppointmentsInternal();
        String lowerQuery = query.toLowerCase();

        return appointments.stream()
        .filter(appointment -> {
            boolean matchesAppointmentId = appointment.getAppointmentId() != null && appointment.getAppointmentId().toLowerCase().contains(lowerQuery);
            boolean matchesDoctor = appointment.getDoctorId() != null && appointment.getDoctorId().toLowerCase().contains(lowerQuery);
            boolean matchesPatient = appointment.getPatientId() != null && appointment.getPatientId().toLowerCase().contains(lowerQuery);
            boolean matchesDate = appointment.getAppointmentDate() != null && appointment.getAppointmentDate().contains(lowerQuery);
            boolean matchesDateTime = (String.format("%d",appointment.getAppointmentHour())).toLowerCase().contains(lowerQuery);

            return matchesAppointmentId || matchesDoctor || matchesPatient || matchesDate || matchesDateTime;
        })
        .map(appointment -> mapper.convertValue(appointment, AppointmentDTO.class))
        .toList();
    }

    public List<AppointmentReportDTO> getAppointmentReports() {

        List<DoctorDTO> doctors = doctorDAO.getAllDoctors();
        List<Appointment> appointments = getAppointmentsInternal();
        List<Appointment> cancelledAppointments = getCancelledAppointmentsInternal();
        List<Appointment> rescheduledAppointments = getRescheduledAppointmentsInternal();

        ArrayList<AppointmentReportDTO> reports = new ArrayList<>();

        for (DoctorDTO doctor : doctors) {
            String doctorId = doctor.getDoctorId();
            int booked = 0;
            int completed = 0;
            int cancelled = 0;
            int rescheduled = 0;

            for (Appointment appointment : appointments) {
                if (doctorId.equals(appointment.getDoctorId())) {
                    booked++;

                    if (Appointment.Status.COMPLETED.toString().equals(appointment.getStatus())) {
                        completed++;
                    }
                }
            }

            for (Appointment appointment : cancelledAppointments) {

                if (doctorId.equals(appointment.getDoctorId())) {
                    cancelled++;
                }
            }

            for (Appointment appointment : rescheduledAppointments) {
                if (doctorId.equals(appointment.getDoctorId())) {
                    rescheduled++;
                }
            }

            reports.add(
                new AppointmentReportDTO(
                    doctorId,
                    doctor.getName(),
                    booked + cancelled,
                    completed,
                    cancelled,
                    rescheduled));
        }

        return reports;
    }

    public int getAppointmentsTodayCount(String doctorName) {
        return (int) getAppointmentsInternal()
                .stream()
                .filter(appointment -> doctorName.equalsIgnoreCase(appointment.getDoctorName()))
                .filter(appointment -> LocalDate.parse(appointment.getAppointmentDate()).equals(LocalDate.now()))
                .count();
    }

    public int getUpcomingAppointmentsCount(String doctorName) {
        return (int) getAppointmentsInternal()
                .stream()
                .filter(appointment -> doctorName.equalsIgnoreCase(appointment.getDoctorName()))
                .filter(appointment -> appointment.getStatus().equalsIgnoreCase("SCHEDULED") || appointment.getStatus().equalsIgnoreCase("RESCHEDULED"))
                .count();
    }

    public int getCompletedTodayCount(String doctorName) {
        return (int) getAppointmentsInternal()
                .stream()
                .filter(appointment -> doctorName.equalsIgnoreCase(appointment.getDoctorName()))
                .filter(appointment -> LocalDate.parse(appointment.getAppointmentDate()).equals(LocalDate.now()))
                .filter(appointment -> appointment.getStatus().equalsIgnoreCase("COMPLETED"))
                .count();
    }

    public String emailsToNotify() {

        List<String> toEmails = getAppointmentsInternal()
                .stream()
                .filter(appointment -> !LocalDate.parse(appointment.getAppointmentDate()).isBefore(LocalDate.now()))
                .filter(appointment -> !LocalDate.parse(appointment.getAppointmentDate()).isAfter(LocalDate.now().plusDays(1)))
                .filter(appointment -> !LocalDate.parse(appointment.getAppointmentDate()).equals(LocalDate.now()))
                .map(appointment -> patientDAO.getEmail(appointment.getPatientId()))
                .filter(email -> !email.isEmpty())
                .map(email -> email.get())
                .toList();

        return String.join(", ", toEmails);
    }
}
