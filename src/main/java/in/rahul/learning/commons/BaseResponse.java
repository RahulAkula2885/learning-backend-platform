package in.rahul.learning.commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class BaseResponse {

    private int status;
    private String message;
    private Object data;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy HH:mm:ss",
            timezone = "Asia/Kolkata")
    private Instant timestamp;

}
