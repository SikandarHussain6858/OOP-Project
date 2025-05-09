package com.example.rpms.model;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotification {

    private final String host = "smtp.gmail.com";
    private final String fromEmail;
    private final String password;
    private final String toEmail;

    public EmailNotification(String fromEmail, String password, String toEmail) {
        this.fromEmail = fromEmail;
        this.password = password;
        this.toEmail = toEmail;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email to " + toEmail);
        }
    }
}

