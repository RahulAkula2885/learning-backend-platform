package in.rahul.learning.controller;

import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.request.LoginRequest;
import in.rahul.learning.model.request.UserRequest;
import in.rahul.learning.model.response.UserResponse;
import in.rahul.learning.service.IUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller responsible for user management operations.
 * <p>
 * Endpoints:
 * - Create User
 * - Update User
 * - Fetch All Users
 * - Fetch User By ID
 * - Login
 */
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(
        name = "User Management",
        description = "APIs for managing users including creation, update, retrieval and authentication"
)
public class UserController {

    private final IUserService userService;

    /**
     * Creates a new user.
     *
     * @param request User creation request
     * @return Success or failure response
     */
    @Operation(
            summary = "Create User",
            description = "Creates a new user after validating the request payload"
    )
    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createUser(@RequestBody UserRequest request) {

        String correlationId = MDC.get("correlationId");

        log.info(
                "Received create user request. CorrelationId={}",
                correlationId
        );

        return userService.createUser(request);
    }

    /**
     * Updates an existing user.
     * <p>
     * Only supplied fields will be updated.
     *
     * @param request User update request
     * @return Success or failure response
     */
    @Operation(
            summary = "Update User",
            description = "Updates an existing user. Only provided fields will be modified"
    )
    @PatchMapping("/update")
    public ResponseEntity<BaseResponse> updateUser(
            @RequestBody User request
    ) {

        log.info("Received update request for userId={}", request);

        return userService.updateUser(request);
    }

    /**
     * Retrieves all active users.
     *
     * @return List of users
     */
    @Operation(
            summary = "Get All Users",
            description = "Returns all active users excluding deleted records"
    )
    @GetMapping
    public List<UserResponse> getUsers() {

        log.info("Fetching all users");

        return userService.getUserDetails();
    }

    /**
     * Retrieves a user by identifier.
     * <p>
     * Hidden from Swagger documentation.
     *
     * @param id User identifier
     * @return User details
     */
    @Hidden
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {

        log.info("Fetching user details for id={}", id);

        return userService.getUserDetailsById(id);
    }

    /**
     * Authenticates user and returns JWT token.
     * <p>
     * Sensitive information should never be logged.
     *
     * @param request Login request
     * @return Authentication response
     */
    @Operation(
            summary = "User Login",
            description = "Authenticates user credentials and returns an access token"
    )
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @RequestBody LoginRequest request
    ) {

        log.info(
                "Login request received for email={}",
                request.email()
        );

        return userService.login(request);
    }
}