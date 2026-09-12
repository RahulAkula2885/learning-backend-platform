package in.rahul.learning.idempotency.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import in.rahul.learning.idempotency.model.request.SendEmailRequest;
import in.rahul.learning.idempotency.service.IdempotencyBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IdempotencyController {

    private final IdempotencyBusinessService idempotencyService;

    @PostMapping("/emails")
    public ResponseEntity<?> sendEmail(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody SendEmailRequest request) throws JsonProcessingException {

        return idempotencyService.sendEmail(
                idempotencyKey,
                request
        );
    }

}
