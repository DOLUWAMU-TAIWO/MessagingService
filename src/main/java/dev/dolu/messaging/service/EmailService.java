package dev.dolu.messaging.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import dev.dolu.messaging.DTO.EmailLog;
import dev.dolu.messaging.repo.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    private final EmailLogRepository emailLogRepository;

    public EmailService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Async
    public void sendEmail(String to, String subject, String contentText) throws IOException {
        logger.info("Preparing to send email to: {}, Subject: {}", to, subject);

        Email from = new Email("service@qorelabs.org");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", contentText);
        Mail mail = new Mail(from, subject, toEmail, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        String status = "SUCCESS";
        String errorMessage = null;
        int statusCode = 0; // To store the status code

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            statusCode = response.getStatusCode();
            logger.info("Email send response - Status Code: {}, Body: {}, Headers: {}",
                    statusCode, response.getBody(), response.getHeaders());

            // Check if the status code indicates success (e.g., 2xx range)
            if (statusCode < 200 || statusCode >= 300) {
                status = "FAILURE";
                errorMessage = "SendGrid API returned an error status: " + statusCode + ", Body: " + response.getBody();
                logger.error("Failed to send email to {}: {}", to, errorMessage);
                throw new IOException(errorMessage); // Optionally throw an exception for the controller to handle
            }

        } catch (IOException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            status = "FAILURE";
            errorMessage = e.getMessage();
        }

        // Log email event to MongoDB
        try {
            logger.info("Saving email log to MongoDB for recipient: {}", to);
            EmailLog emailLog = new EmailLog(
                    to,
                    subject,
                    contentText,
                    LocalDateTime.now(),
                    status,
                    errorMessage
            );
            emailLogRepository.save(emailLog);
            logger.info("Successfully saved email log for: {}", to);
        } catch (Exception ex) {
            logger.error("Failed to save email log for {}: {}", to, ex.getMessage(), ex);
        }

        // Keep this part if you want to throw an exception for actual IO errors
        if ("FAILURE".equals(status) && errorMessage != null && errorMessage.startsWith("Failed to send email")) {
            throw new RuntimeException("Failed to send email: " + errorMessage);
        }
    }

    @Async
    public void sendBulkEmail(List<String> recipients, String subject, String contentText) throws IOException {
        logger.info("Preparing to send bulk email to {} recipients, Subject: {}", recipients.size(), subject);

        Email from = new Email("service@qorelabs.org");
        Content content = new Content("text/plain", contentText);
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);

        Personalization personalization = new Personalization();
        for (String email : recipients) {
            personalization.addTo(new Email(email));
        }
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        String status = "SUCCESS";
        String errorMessage = null;
        int statusCode = 0; // To store the status code

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            statusCode = response.getStatusCode();
            logger.info("Bulk email send response - Status Code: {}, Body: {}, Headers: {}",
                    statusCode, response.getBody(), response.getHeaders());

            // Check if the status code indicates success (e.g., 2xx range)
            if (statusCode < 200 || statusCode >= 300) {
                status = "FAILURE";
                errorMessage = "SendGrid API returned an error status: " + statusCode + ", Body: " + response.getBody();
                logger.error("Failed to send bulk email: {}", errorMessage);
                throw new IOException(errorMessage); // Optionally throw an exception
            }

        } catch (IOException e) {
            logger.error("Failed to send bulk email: {}", e.getMessage(), e);
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw new RuntimeException("Failed to send bulk email: " + e.getMessage());
        } finally {
            // Log bulk email event to MongoDB
            EmailLog emailLog = new EmailLog(
                    "bulk",
                    subject,
                    contentText,
                    LocalDateTime.now(),
                    status,
                    errorMessage
            );
            emailLogRepository.save(emailLog);
        }
    }
}