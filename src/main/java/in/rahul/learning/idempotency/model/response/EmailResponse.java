package in.rahul.learning.idempotency.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class EmailResponse {

    private String message;
    private String status;

}
