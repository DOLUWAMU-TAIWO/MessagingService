package dev.dolu.messaging.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Async
    public void sendEmail(String to, String subject, String contentText) throws IOException {
        Email from = new Email("service@qorelabs.org");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", contentText);
        Mail mail = new Mail(from, subject, toEmail, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Email send response - Status Code: {}, Body: {}, Headers: {}",
                    response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (IOException e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
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
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Bulk email send response - Status Code: {}, Body: {}, Headers: {}",
                    response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (IOException e) {
            logger.error("Failed to send bulk email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send bulk email: " + e.getMessage());
        }
    }
}