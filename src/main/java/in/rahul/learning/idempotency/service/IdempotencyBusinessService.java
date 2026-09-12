package in.rahul.learning.idempotency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.rahul.learning.idempotency.model.request.SendEmailRequest;
import in.rahul.learning.idempotency.model.response.EmailResponse;
import in.rahul.learning.idempotency.repository.IdempotencyRepository;
import in.rahul.learning.util.CommonUtils;
import in.rahul.learning.util.IdempotencyRecord;
import in.rahul.learning.util.IdempotencyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotencyBusinessService {

    private final IdempotencyRepository idempotencyRepository;
    private final CommonUtils commonUtils;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseEntity<?> sendEmail(
            String idempotencyKey,
            SendEmailRequest request) throws JsonProcessingException {

        String requestHash =
                commonUtils.generateIdempotencyHash(idempotencyKey,request);

        // Try to create idempotency record
        int inserted =
                idempotencyRepository.createIfAbsent(
                        idempotencyKey,
                        requestHash
                );

        if (inserted == 0) {

            // Already exists
            IdempotencyRecord existing =
                    idempotencyRepository
                            .findByIdempotencyKey(idempotencyKey)
                            .orElseThrow();

            // Same key but different request
            if (!existing.getRequestHash()
                    .equals(requestHash)) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Idempotency-Key reused for different request");
            }

            // Already completed
            if (existing.getStatus()
                    == IdempotencyStatus.COMPLETED) {

                try {

                    Object response =
                            objectMapper.readValue(
                                    existing.getResponseBody(),
                                    Object.class
                            );

                    return ResponseEntity
                            .status(existing.getResponseStatus())
                            .body(response);

                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }

            // Currently processing
            if (existing.getStatus()
                    == IdempotencyStatus.IN_PROGRESS) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Request is already being processed");
            }
        }

        // =================================================
        // Actual business logic
        // =================================================

        try {

            // Send email
            EmailResponse response =
                    actuallySendEmail(request);

            String responseJson =
                    objectMapper.writeValueAsString(response);

            // Mark completed
            IdempotencyRecord record =
                    idempotencyRepository
                            .findByIdempotencyKey(idempotencyKey)
                            .orElseThrow();

            record.setStatus(
                    IdempotencyStatus.COMPLETED
            );

            record.setResponseStatus(
                    HttpStatus.OK.value()
            );

            record.setResponseBody(
                    responseJson
            );

            idempotencyRepository.save(record);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            IdempotencyRecord record =
                    idempotencyRepository
                            .findByIdempotencyKey(idempotencyKey)
                            .orElseThrow();

            record.setStatus(
                    IdempotencyStatus.FAILED
            );

            idempotencyRepository.save(record);

            throw e;
        }
    }

    private EmailResponse actuallySendEmail(
            SendEmailRequest request) {

        // Actual email sending implementation

        return new EmailResponse(
                "SUCCESS",HttpStatus.OK.toString()
        );
    }
}
