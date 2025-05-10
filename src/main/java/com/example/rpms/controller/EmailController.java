package com.example.rpms.controller;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeBodyPart;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.rpms.model.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailController {
    @FXML private TextField toField;
    @FXML private TextField ccField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyField;
    @FXML private Label emailStatusLabel;
    @FXML private Button sendButton;
    @FXML private Button attachButton;
    @FXML private ListView<String> attachmentList;
    @FXML private CheckBox htmlCheckBox;
    @FXML private ProgressIndicator progressIndicator;
    
    private String userId;

    private List<File> attachments = new ArrayList<>();
    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        setupAttachmentHandling();
    }

    private void setupAttachmentHandling() {
        attachButton.setOnAction(e -> handleAttachment());
    }

    private void handleAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Attachment");
        File file = fileChooser.showOpenDialog(attachButton.getScene().getWindow());
        
        if (file != null) {
            attachments.add(file);
            attachmentList.getItems().add(file.getName());
        }
    }

    @FXML
    private void handleSendEmail() {
        String to = toField.getText().trim();
        String cc = ccField.getText().trim();
        String subject = subjectField.getText().trim();
        String messageBody = bodyField.getText().trim();

        if (to.isEmpty() || messageBody.isEmpty() || subject.isEmpty()) {
            showStatus("Please fill in required fields.", true);
            return;
        }

        sendButton.setDisable(true);
        progressIndicator.setVisible(true);

        // Use ExecutorService for non-blocking email sending
        emailExecutor.submit(() -> {
            try {
                sendEmailWithAttachments(to, cc, subject, messageBody);
                javafx.application.Platform.runLater(() -> 
                    showStatus("Email sent successfully!", false));
            } catch (MessagingException e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> 
                    showStatus("Failed to send email: " + e.getMessage(), true));
            } finally {
                javafx.application.Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    progressIndicator.setVisible(false);
                });
            }
        });
    }

    private void sendEmailWithAttachments(String to, String cc, String subject, String messageBody) 
            throws MessagingException {
        // Email configuration - Consider moving to properties file
        final String fromEmail = "sikandarhussain6858358@gmail.com"; // Move to config
        final String password = "ssffpmdbulqinfst";     // Move to config

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.connectiontimeout", "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        
        if (!cc.isEmpty()) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        
        message.setSubject(subject);

        // Create message body
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        if (htmlCheckBox.isSelected()) {
            messageBodyPart.setContent(messageBody, "text/html; charset=utf-8");
        } else {
            messageBodyPart.setText(messageBody);
        }

        // Create multipart message
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Add attachments
        for (File file : attachments) {
            MimeBodyPart attachPart = new MimeBodyPart();
            try {
                attachPart.attachFile(file);
                multipart.addBodyPart(attachPart);
            } catch (IOException e) {
                e.printStackTrace();
                throw new MessagingException("Failed to attach file: " + file.getName(), e);
            }
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    private void showStatus(String message, boolean isError) {
        emailStatusLabel.setText(message);
        emailStatusLabel.setTextFill(isError ? 
            javafx.scene.paint.Color.RED : 
            javafx.scene.paint.Color.GREEN);
    }

    @FXML
    private void handleClear() {
        toField.clear();
        ccField.clear();
        subjectField.clear();
        bodyField.clear();
        attachments.clear();
        attachmentList.getItems().clear();
        emailStatusLabel.setText("");
    }

    public void cleanup() {
        emailExecutor.shutdown();
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public void prepopulateEmergencyResponse(String alertType, String alertMessage) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT u.email, u.username FROM users u WHERE u.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String recipientEmail = rs.getString("email");
                String recipientName = rs.getString("username");
                toField.setText(recipientEmail);
                subjectField.setText("RE: " + alertType + " - Emergency Alert Response");
                
                // Create a professional response template
                String template = """
                    Dear %s,
                    
                    This is in response to your emergency alert regarding: %s
                    
                    Alert Details:
                    %s
                    
                    I have received and reviewed your emergency alert. I am reaching out to provide immediate assistance.
                    
                    Please follow any provided instructions carefully. If your condition worsens or you need immediate medical attention, please call emergency services immediately.
                    
                    Best regards,
                    [Your Doctor]
                    """.formatted(recipientName, alertType, alertMessage);
                
                bodyField.setText(template);
            }
        } catch (SQLException e) {
            showStatus("Error loading recipient details: " + e.getMessage(), true);
        }
    }

    public void prepareVideoCallEmail(String recipientEmail, String subject, String messageTemplate) {
        toField.setText(recipientEmail);
        subjectField.setText(subject);
        bodyField.setText(messageTemplate);
        htmlCheckBox.setSelected(false); // Plain text for better compatibility
    }
}
