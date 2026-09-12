package in.rahul.learning.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

@Component
public class CommonUtils {

    private final ObjectMapper objectMapper;

    public CommonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generateIdempotencyHash(
            String idempotencyKey,
            Object request) {

        try {
            String requestJson =
                    objectMapper.writeValueAsString(request);

            String input = idempotencyKey
                            + "|"
                            + requestJson;

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            input.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to generate idempotency hash",
                    e
            );
        }
    }

    public String generateRequestHash(Object request) {
        try {
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    json.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to generate idempotency key", e
            );
        }
    }


    String createdDbRecord = """
                
                if request failed then again user need to hit the request so that idempotency will be reprocessed
                
                
                CREATE TABLE idempotency_record (
                    id BIGINT PRIMARY KEY,
                    idempotency_key VARCHAR(255) NOT NULL,
                    request_hash VARCHAR(64) NOT NULL,
                    status VARCHAR(30) NOT NULL,
                    response_body TEXT,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL,
            
                    CONSTRAINT uk_idempotency_key
                        UNIQUE (idempotency_key)
                );
            
            Possible statuses:
                    IN_PROGRESS
                    COMPLETED
                    FAILED
            
            
            """;

}
