package dev.dolu.messaging.controller.EmailController;

import dev.dolu.messaging.DTO.BulkEmailRequest;
import dev.dolu.messaging.DTO.SingleEmailRequest;
import dev.dolu.messaging.metrics.EmailMetrics;
import dev.dolu.messaging.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

            // 🟢 Count success
            emailMetrics.incrementEmailSuccessCount();

            logger.info("Email sent successfully to: {}", request.getTo());
            return ResponseEntity.ok("Email sent successfully to " + request.getTo());
        } catch (Exception e) {
            // 🔴 Count failure
            emailMetrics.incrementEmailFailureCount();

            logger.error("Failed to send email to {}: {}", request.getTo(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
}