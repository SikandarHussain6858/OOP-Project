package com.example.rpms.controller;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Properties;



public class EmailController {
    @FXML
    private TextField toField;

    @FXML
    private TextArea bodyField;

    @FXML
    private Label emailStatusLabel;

    @FXML
    private void handleSendEmail() {
        String to = toField.getText().trim();
        String messageBody = bodyField.getText().trim();

        if (to.isEmpty() || messageBody.isEmpty()) {
            emailStatusLabel.setText("Please fill in all fields.");
            emailStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        // Email configuration
        final String fromEmail = "sikandarhussain6858358@gmail.com"; // Replace with your sender email
        final String password = "ssffpmdbulqinfst";     // Use an App Password if 2FA is enabled

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("RPMS Notification");
            message.setText(messageBody);

            Transport.send(message);
            emailStatusLabel.setText("Email sent successfully!");
            emailStatusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        } catch (MessagingException e) {
            e.printStackTrace();
            emailStatusLabel.setText("Failed to send email.");
            emailStatusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}
