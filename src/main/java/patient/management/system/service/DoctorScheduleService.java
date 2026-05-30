package patient.management.system.service;

import patient.management.system.dao.DoctorScheduleDAO;
import patient.management.system.model.DoctorSchedule;

import java.util.List;

public class DoctorScheduleService {

    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();

    public void addSchedule(String doctorId, DoctorSchedule.Day startDay, DoctorSchedule.Day endDay, DoctorSchedule.Shift shift) {

        try {
            doctorScheduleDAO.addSchedule(doctorId, startDay, endDay, shift);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateDoctorSchedule(String doctorId, DoctorSchedule.Day startDay, DoctorSchedule.Day endDay, DoctorSchedule.Shift shift) {
        try {
            doctorScheduleDAO.updateDoctorSchedule(doctorId, startDay, endDay, shift);
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