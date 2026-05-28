package patient.management.system.service;

import patient.management.system.dao.ReceptionistDAO;
import patient.management.system.dto.ReceptionistDTO;

import java.util.List;

public class ReceptionistService {
    ReceptionistDAO receptionistDAO = new ReceptionistDAO();

    /**
     * @param id receptionist ID or user ID
     */
    public void activateReceptionist(String id) {
        try {
            receptionistDAO.activateReceptionist(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id receptionist ID or user ID
     */
    public void inactivateReceptionist(String id) {
        try {
            receptionistDAO.inactivateReceptionist(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<ReceptionistDTO> getActiveReceptionists() {
        try {
            return receptionistDAO.getActiveReceptionists();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<ReceptionistDTO> getInactiveReceptionists() {
        try {
            return receptionistDAO.getInactiveReceptionists();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
