package dev.dolu.messaging.controller.EmailController;


import dev.dolu.messaging.DTO.BulkEmailRequest;
import dev.dolu.messaging.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bulk-email")
public class BulkEmailController {

    private static final Logger logger = LoggerFactory.getLogger(BulkEmailController.class);
    private final EmailService emailService;

    public BulkEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendBulkEmail(@RequestBody BulkEmailRequest bulkEmailRequest) {
        int count = bulkEmailRequest.getRecipients() != null ? bulkEmailRequest.getRecipients().size() : 0;
        logger.info("Received bulk email request for {} recipients", count);
        try {
            emailService.sendBulkEmail(bulkEmailRequest.getRecipients(), bulkEmailRequest.getSubject(), bulkEmailRequest.getContent());
            logger.info("Bulk email sent successfully to {} recipients", count);
            return ResponseEntity.ok("Bulk email sent successfully to " + count + " recipients.");
        } catch (Exception e) {
            logger.error("Failed to send bulk email: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to send bulk email: " + e.getMessage());
        }
    }
}