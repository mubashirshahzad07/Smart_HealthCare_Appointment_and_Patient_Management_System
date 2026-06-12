package patient.management.system.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import patient.management.system.dao.IdDAO;

@JsonIgnoreProperties({ "password", "role" })
public class Receptionist extends User {
    private String receptionistId;
    private Shift shift;
    private boolean isActive;

    public enum Shift {
        MORNING,
        EVENING,
        NIGHT
    }

    public Receptionist() {
    }

    public Receptionist(String username, String password, String name, Shift shift) {
        super(username, name, password, Role.RECEPTIONIST);
        this.shift = shift;
        this.receptionistId = IdDAO.getReceptionistId();
        this.isActive = true;
    }

    public String getReceptionistId() {
        return receptionistId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getShift() {
        return shift.toString();
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}