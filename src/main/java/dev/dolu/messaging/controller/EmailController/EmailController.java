package dev.dolu.messaging.controller.EmailController;

import dev.dolu.messaging.DTO.BulkEmailRequest;
import dev.dolu.messaging.DTO.SingleEmailRequest;
import dev.dolu.messaging.metrics.EmailMetrics;
import dev.dolu.messaging.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
    private final EmailService emailService;
    private final EmailMetrics emailMetrics;

    public EmailController(EmailService emailService, EmailMetrics emailMetrics) {
        this.emailService = emailService;
        this.emailMetrics = emailMetrics;
    }


    @GetMapping("/")
    public ResponseEntity<String> rootEndpoint() {
        return ResponseEntity.ok("Welcome to Email Service!");
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is up and running!");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody SingleEmailRequest request) {
        logger.info("Received request to send email to: {}", request.getTo());

        // 🟡 Count attempt immediately
        emailMetrics.incrementEmailAttemptCount();

        try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getContent());

            // 🟢 Count success (request accepted by SendGrid)
            emailMetrics.incrementEmailSuccessCount();

            logger.info("Email request processed for: {}", request.getTo());
            return ResponseEntity.ok("Email request processed successfully for " + request.getTo());
        } catch (IOException e) {
            // 🔴 Count failure (SendGrid returned an error)
            emailMetrics.incrementEmailFailureCount();
            logger.error("Failed to send email to {} due to SendGrid error: {}", request.getTo(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Failed to send email due to SendGrid error: " + e.getMessage());
        } catch (Exception e) {
            // 🔴 Count failure (Other errors)
            emailMetrics.incrementEmailFailureCount();
            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkEmail(@RequestBody BulkEmailRequest request) {
        logger.info("Received request to send bulk email to {} recipients", request.getRecipients().size());

        // 🟡 Count attempt immediately (for the entire bulk operation)
        emailMetrics.incrementEmailAttemptCount(); // Increment attempt count once for the bulk operation

        try {
            emailService.sendBulkEmail(request.getRecipients(), request.getSubject(), request.getContent());

            // 🟢 Count success (request accepted by SendGrid)
            emailMetrics.incrementEmailSuccessCount(); // Increment success count once for the bulk operation
            logger.info("Bulk email request processed successfully for {} recipients", request.getRecipients().size());
            return ResponseEntity.ok("Bulk email request processed successfully for " + request.getRecipients().size() + " recipients");
        } catch (IOException e) {
            // 🔴 Count failure (SendGrid returned an error)
            emailMetrics.incrementEmailFailureCount(); // Increment failure count once for the bulk operation
            logger.error("Failed to send bulk email due to SendGrid error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Failed to send bulk email due to SendGrid error: " + e.getMessage());
        } catch (Exception e) {
            // 🔴 Count failure (Other errors)
            emailMetrics.incrementEmailFailureCount(); // Increment failure count once for the bulk operation
            logger.error("Failed to send bulk email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send bulk email: " + e.getMessage());
        }
    }
}