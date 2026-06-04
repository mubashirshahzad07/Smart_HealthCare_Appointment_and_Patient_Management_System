package patient.management.system.service;

import patient.management.system.dao.AppointmentDAO;
import patient.management.system.dao.NotificationDAO;

public class NotificationService {

    public void sendReminder() {
        try {

            String emails = new AppointmentDAO().emailsToNotify();
            Thread reminderThread = new Thread() {
                @Override
                public void run() {
                    NotificationDAO.sendEmail(emails, NotificationDAO.REMINDER_MESSAGE);
                }
            };
            reminderThread.start();

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean isValidEmail(String to)  {
        return NotificationDAO.isValidEmail(to);
    }
}
