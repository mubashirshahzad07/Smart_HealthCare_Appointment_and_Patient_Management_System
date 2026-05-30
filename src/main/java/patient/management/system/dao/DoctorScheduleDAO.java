package patient.management.system.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import patient.management.system.dto.DoctorDTO;
import patient.management.system.model.DoctorSchedule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorScheduleDAO {

    private final ObjectMapper mapper = new ObjectMapper();
    private final File file = new File("data/doctor_schedules.json");

    public void addSchedule(String doctorId, DoctorSchedule.Day startDay, DoctorSchedule.Day endDay, DoctorSchedule.Shift shift) {

        doctorRegistered(doctorId);
        ArrayList<DoctorSchedule> schedules = getSchedulesInternal();

        DoctorSchedule.Day[] days = DoctorSchedule.Day.values();

        int start = startDay.ordinal();
        int end = endDay.ordinal();

        if (start > end) {
            throw new RuntimeException("Start day must come before end day.");
        }

        for (int i = start; i <= end; i++) {
            DoctorSchedule.Day day = days[i];
            duplicateSchedule(schedules, doctorId, day, shift);
            schedules.add(new DoctorSchedule(doctorId, day, shift));
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, schedules);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save doctor schedule.");
        }
    }

    // public void updateDoctorSchedule(String doctorId, List<DoctorSchedule> updatedSchedules) {

    //     if (updatedSchedules == null || updatedSchedules.isEmpty()) {
    //         throw new RuntimeException("Doctor must have at least one schedule.");
    //     }

    //     doctorRegistered(doctorId);
    //     ArrayList<DoctorSchedule> schedules = getSchedulesInternal();
    //     schedules.removeIf(schedule -> schedule.getDoctorId().equals(doctorId));

    //     for (DoctorSchedule schedule : updatedSchedules) {

    //         duplicateSchedule(schedules, doctorId, schedule.getDay(), schedule.getShift());

    //         schedules.add(new DoctorSchedule(doctorId, schedule.getDay(), schedule.getShift()));
    //     }

    //     try {
    //         mapper.writerWithDefaultPrettyPrinter().writeValue(file, schedules);
    //     } catch (IOException e) {
    //         throw new RuntimeException("Unable to update schedules.");
    //     }
    // }

    public void updateDoctorSchedule(String doctorId, DoctorSchedule.Day startDay, DoctorSchedule.Day endDay, DoctorSchedule.Shift shift, double fee) {

        doctorRegistered(doctorId);
        ArrayList<DoctorSchedule> schedules = getSchedulesInternal();
        new DoctorDAO().updateDoctorFee(doctorId, fee);

        schedules.removeIf(schedule -> schedule.getDoctorId().equals(doctorId));

        DoctorSchedule.Day[] days = DoctorSchedule.Day.values();

        int start = startDay.ordinal();
        int end = endDay.ordinal();

        if (start > end) {
            throw new RuntimeException("Start day must come before end day.");
        }

        for (int i = start; i <= end; i++) {
            DoctorSchedule.Day day = days[i];
            duplicateSchedule(schedules, doctorId, day, shift);
            schedules.add(new DoctorSchedule(doctorId, day, shift));
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, schedules);
        } catch (IOException e) {
            throw new RuntimeException("Unable to update schedules.");
        }
    }

    public List<DoctorSchedule> getSchedules() {
        return getSchedulesInternal();
    }

    private ArrayList<DoctorSchedule> getSchedulesInternal() {

        try {

            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    new TypeReference<ArrayList<DoctorSchedule>>() {
                    });

        } catch (IOException e) {

            throw new RuntimeException("Unable to load schedules.");
        }
    }

    private void doctorRegistered(String doctorId) {

        DoctorDAO doctorDAO = new DoctorDAO();
        List<DoctorDTO> doctors = doctorDAO.getActiveDoctors();

        for (DoctorDTO doctor : doctors) {

            if (doctor.getDoctorId().equals(doctorId)) {

                return;
            }
        }

        throw new RuntimeException("Doctor is not registered.");
    }

    private void duplicateSchedule(
            List<DoctorSchedule> schedules, String doctorId,
            DoctorSchedule.Day day, DoctorSchedule.Shift shift) {

        for (DoctorSchedule schedule : schedules) {

            boolean duplicate = schedule.getDoctorId().equals(doctorId)
                    && schedule.getDay() == day
                    && schedule.getShift() == shift;

            if (duplicate) {
                throw new RuntimeException("Duplicate doctor schedule.");
            }
        }
    }

    public List<DoctorSchedule> getDoctorSchedules(String doctorId) {

        doctorRegistered(doctorId);
        ArrayList<DoctorSchedule> doctorSchedules = new ArrayList<>();

        for (DoctorSchedule schedule : getSchedulesInternal()) {

            if (schedule.getDoctorId().equals(doctorId)) {
                doctorSchedules.add(schedule);
            }
        }

        return doctorSchedules;
    }
}
