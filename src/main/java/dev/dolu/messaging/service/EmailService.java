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
        Email from = new Email("service@qorelabs.org");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", contentText);
        Mail mail = new Mail(from, subject, toEmail, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        String status = "SUCCESS";
        String errorMessage = null;

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Email send response - Status Code: {}, Body: {}, Headers: {}",
                    response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (IOException e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        } finally {
            // Log email event to MongoDB
            EmailLog emailLog = new EmailLog(
                    to,
                    subject,
                    contentText,
                    LocalDateTime.now(),
                    status,
                    errorMessage
            );
            emailLogRepository.save(emailLog);
        }
    }

    @Async
    public void sendBulkEmail(List<String> recipients, String subject, String contentText) throws IOException {
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

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Bulk email send response - Status Code: {}, Body: {}, Headers: {}",
                    response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (IOException e) {
            logger.error("Failed to send bulk email: {}", e.getMessage(), e);
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw new RuntimeException("Failed to send bulk email: " + e.getMessage());
        } finally {
            // Log bulk email event to MongoDB (logged as one record for the entire operation)
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