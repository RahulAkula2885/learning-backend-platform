package in.rahul.learning.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Component
public class CommonUtils {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

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


    public static String encode(long value){

        StringBuilder sb = new StringBuilder();

        while (value > 0){
            sb.append(BASE62.charAt((int)(value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }

    public static String generateShortCode(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    originalUrl.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash)
                    .substring(0, 8);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate short code", e);
        }
    }


    public static String generateShortCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 7);
    }

}
