package patient.management.system.model;

public class DoctorSchedule {

    private String doctorId;
    private Day day;
    private Shift shift;

    public enum Shift {
        MORNING,
        EVENING
    }

    public enum Day {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    public DoctorSchedule() {}

    public DoctorSchedule(String doctorId, Day day, Shift shift) {

        this.doctorId = doctorId;
        this.day = day;
        this.shift = shift;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public Day getDay() {
        return day;
    }

    public Shift getShift() {
        return shift;
    }
}
