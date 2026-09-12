package in.rahul.learning.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import in.rahul.learning.model.enums.UserRole;

import java.time.Instant;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import in.rahul.learning.model.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JacksonXmlRootElement(localName = "UserResponse")
public class UserResponseXml {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String phoneNo;
    private Boolean active;
    private Boolean deleted;
    private Instant createdTime;
    private Instant modifiedTime;

}