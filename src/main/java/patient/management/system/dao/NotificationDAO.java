package patient.management.system.dao;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Properties;

public class NotificationDAO {
    public static final String REMINDER_MESSAGE = """
            Reminder: You have an appointment scheduled for tomorrow.

            Please arrive 10–15 minutes early. If you are unable to attend, kindly contact us as soon as possible to reschedule or cancel your appointment. \

            Thank you.
            """;

    public static final String RESCHEDULE_CONFIRMATION = """
            Your appointment has been successfully rescheduled.

            If you have any questions or need to make further changes, please contact us.

            Thank you.
            """;

    public static final String CANCEL_CONFIRMATION = """
            Your appointment has been successfully cancelled.

            If you would like to schedule a new appointment in the future, please contact us.

            Thank you.
            """;
                    
    /**
     * sends email given the recipient addresses
     * 
     * @param to recipient addresses (format: "email(1), email(2), email(3), ... ,
     *           email(n)")
     */
    public static void sendEmail(String to, String notficationMessage) {
        if (!isValidEmail(to)) {
            throw new RuntimeException("Email address is not valid.");
        }

        String host = "smtp.gmail.com";
        String from = "patientmanagementsystem2026@gmail.com";

        Dotenv dotenv = Dotenv.load();
        String password = dotenv.get("APP_PASSWORD");

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(to));
            message.setSubject("Hello");
            message.setText(notficationMessage);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if the emails are valid
     * 
     * @param to recipient addresses (format: "email(1), email(2), email(3), ... ,
     *           email(n)")
     * @return true if emails are valid, false otherwise
     */
    public static boolean isValidEmail(String to) {
        String[] recipientEmails = to.split(",");

        for (String recipientEmail : recipientEmails) {
            if (!recipientEmail.contains("@gmail.com")) {
                return false;
            }
        }

        return true;
    }
}
