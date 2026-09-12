package in.rahul.learning.idempotency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.rahul.learning.idempotency.repository.IdempotencyRepository;
import in.rahul.learning.util.CommonUtils;
import in.rahul.learning.util.IdempotencyRecord;
import in.rahul.learning.util.IdempotencyStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repository;
    private final CommonUtils commonUtils;
    private final ObjectMapper objectMapper;

    @Transactional
    public Optional<ResponseEntity<?>> checkExistingRequest(
            String idempotencyKey,
            Object request) {

        String requestHash =
                commonUtils.generateIdempotencyHash(idempotencyKey,request);

        Optional<IdempotencyRecord> existing =
                repository.findByIdempotencyKey(idempotencyKey);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        IdempotencyRecord record = existing.get();

        // Same idempotency key but different request
        if (!record.getRequestHash().equals(requestHash)) {

            throw new IllegalStateException(
                    "Idempotency-Key already used for a different request"
            );
        }

        // Request is currently being processed
        if (record.getStatus() ==
                IdempotencyStatus.IN_PROGRESS) {

            return Optional.of(
                    ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Request is already being processed")
            );
        }

        // Request was completed previously
        if (record.getStatus() ==
                IdempotencyStatus.COMPLETED) {

            try {

                Object response =
                        objectMapper.readValue(
                                record.getResponseBody(),
                                Object.class
                        );

                return Optional.of(
                        ResponseEntity
                                .status(record.getResponseStatus())
                                .body(response)
                );

            } catch (Exception e) {
                throw new IllegalStateException(
                        "Unable to deserialize stored response",
                        e
                );
            }
        }

        // FAILED
        return Optional.empty();
    }
}
