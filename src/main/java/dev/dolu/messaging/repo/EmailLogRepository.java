package dev.dolu.messaging.repo;

import dev.dolu.messaging.DTO.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
    // You can add custom query methods if needed later
}