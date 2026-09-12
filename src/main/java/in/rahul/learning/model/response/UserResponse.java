package in.rahul.learning.model.response;

import in.rahul.learning.model.enums.UserRole;

import java.time.Instant;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "user")
public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        String phoneNo,
        Boolean active,
        Boolean deleted,
        Instant createdTime,
        Instant modifiedTime
) {
}