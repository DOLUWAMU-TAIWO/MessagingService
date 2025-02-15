package dev.dolu.messaging.controller.EmailController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTestController {

    @GetMapping("/api-test")
    public String apiTest() {
        return "<html><body style=\"font-family: Arial; text-align:center; padding: 20px;\">"
                + "<h2>🌐 API Security Test</h2>"
                + "<p>This is an API security test for <strong>MessagingService</strong>.</p>"
                + "<p>Status: <span style=\"color:green; font-weight:bold;\">Secure ✅</span></p>"
                + "</body></html>";
    }
}