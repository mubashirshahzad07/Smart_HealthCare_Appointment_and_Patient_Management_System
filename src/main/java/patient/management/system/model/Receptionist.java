package patient.management.system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import patient.management.system.dao.IdDao;

@JsonIgnoreProperties({"password", "role"})
public class Receptionist extends User {
    private String receptionistId;
    private String name;
    private Shift shift;

    public enum Shift {
        MORNING,
        EVENING,
        NIGHT
    }

    public Receptionist() {}

    public Receptionist(String username, String password, String name, Shift shift) {
        super(username, name, password, Role.RECEPTIONIST);
        this.shift = shift;
        this.receptionistId = IdDao.getReceptionistId();
    }

    public String getReceptionistId() {
        return receptionistId;
    }

    public String getShift() {
        return shift.toString();
    }
}