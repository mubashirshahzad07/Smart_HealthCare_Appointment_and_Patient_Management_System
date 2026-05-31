package patient.management.system.service;

import patient.management.system.dao.NotificationDAO;

public class NotificationService {
    public void sendEmail(String to) {
        try {
            NotificationDAO.sendEmail(to);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean isValidEmail(String to)  {
        return NotificationDAO.isValidEmail(to);
    }
}
