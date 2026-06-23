package in.rahul.learning.model.request;

import in.rahul.learning.model.enums.UserRole;


public record UserRequest(

        String name,
        String phoneNo,
        UserRole role,
        String email,
        String password
) {
}
