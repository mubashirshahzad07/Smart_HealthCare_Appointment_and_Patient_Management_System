package patient.management.system.service;

import patient.management.system.dao.DoctorScheduleDAO;
import patient.management.system.model.DoctorSchedule;

import java.util.List;

public class DoctorScheduleService {

    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();

    public void addSchedule(String doctorId, DoctorSchedule.Day day, DoctorSchedule.Shift shift) {

        try {
            doctorScheduleDAO.addSchedule(doctorId, day, shift);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateDoctorSchedule(String doctorId, List<DoctorSchedule> updatedSchedules) {

        try {
            doctorScheduleDAO.updateDoctorSchedule(doctorId, updatedSchedules);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorSchedule> getDoctorSchedules(String doctorId) {

        try {
            return doctorScheduleDAO.getDoctorSchedules(doctorId);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<DoctorSchedule> getSchedules() {

        try {
            return doctorScheduleDAO.getSchedules();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}