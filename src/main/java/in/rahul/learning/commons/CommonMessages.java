package in.rahul.learning.commons;

import org.springframework.stereotype.Component;

@Component
public final class CommonMessages {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";

    // Authentication
    public static final String INVALID_LOGIN_CREDENTIALS = "Invalid login credentials";
    public static final String EMAIL_PASSWORD_MISMATCH = "Email or password is incorrect";

    // Required fields
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String NAME_REQUIRED = "Name is required";
    public static final String PHONE_NUMBER_REQUIRED = "Phone number is required";
    public static final String ID_REQUIRED = "Id is required";

    // Validation rules
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String INVALID_ROLE = "Invalid role. Allowed values: ROLE_USER, ROLE_ADMIN, ROLE_MANAGER";
    public static final String INVALID_PASSWORD_FORMAT =
            "Password must be at least 8 characters and contain uppercase, lowercase, and a number";

    // Business validations
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";

    private CommonMessages() {
        /* This utility class should not be instantiated */
    }

}
