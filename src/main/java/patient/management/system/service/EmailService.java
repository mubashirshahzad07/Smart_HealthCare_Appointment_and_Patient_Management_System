package patient.management.system.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import io.github.cdimascio.dotenv.Dotenv;

public class EmailService {
    /**
     * sends email given the recipient addresses
     * @param to recipient addresses (format: "email(1), email(2), email(3), ... , email(n)")
     */
    public static void sendEmail(String to) {
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
            message.setText("This is test email.");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if the emails are valid
     * @param to recipient addresses (format: "email(1), email(2), email(3), ... , email(n)")
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