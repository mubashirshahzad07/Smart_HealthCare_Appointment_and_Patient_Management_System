package patient.management.system.service;

import patient.management.system.dao.DoctorScheduleDAO;
import patient.management.system.model.DoctorSchedule;

import java.util.List;

public class DoctorScheduleService {

    private final DoctorScheduleDAO doctorScheduleDAO = new DoctorScheduleDAO();

    public void addSchedule(String doctorId, String startDay, String endDay, String shift) {

        try {
            doctorScheduleDAO.addSchedule(
                doctorId, 
                DoctorSchedule.Day.valueOf(startDay.toUpperCase()),
                DoctorSchedule.Day.valueOf(endDay.toUpperCase()),
                DoctorSchedule.Shift.valueOf(shift.toUpperCase())
            );
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateDoctorSchedule(String doctorId, String startDay, String endDay, String shift) {

        try {
            doctorScheduleDAO.updateDoctorSchedule(
                doctorId, 
                DoctorSchedule.Day.valueOf(startDay.toUpperCase()),
                DoctorSchedule.Day.valueOf(endDay.toUpperCase()),
                DoctorSchedule.Shift.valueOf(shift.toUpperCase())
            );
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