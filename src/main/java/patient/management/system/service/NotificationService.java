package patient.management.system.service;

import patient.management.system.dao.AppointmentDAO;
import patient.management.system.dao.NotificationDAO;

public class NotificationService {

    public void sendEmail() {
        try {
            NotificationDAO.sendEmail(new AppointmentDAO().emailsToNotify());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean isValidEmail(String to)  {
        return NotificationDAO.isValidEmail(to);
    }
}
