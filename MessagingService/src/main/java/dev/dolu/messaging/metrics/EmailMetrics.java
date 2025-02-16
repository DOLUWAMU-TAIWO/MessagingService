package dev.dolu.messaging.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class EmailMetrics {

    private final MeterRegistry meterRegistry;
    private Counter emailAttemptCounter;
    private Counter emailSuccessCounter;
    private Counter emailFailureCounter;

    public EmailMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        this.emailAttemptCounter = meterRegistry.counter("email_attempts_total", "type", "attempt");
        this.emailSuccessCounter = meterRegistry.counter("email_success_total", "type", "success");
        this.emailFailureCounter = meterRegistry.counter("email_failures_total", "type", "failure");
    }

    public void incrementEmailAttemptCount() {
        emailAttemptCounter.increment();
    }

    public void incrementEmailSuccessCount() {
        emailSuccessCounter.increment();
    }

    public void incrementEmailFailureCount() {
        emailFailureCounter.increment();
    }
}