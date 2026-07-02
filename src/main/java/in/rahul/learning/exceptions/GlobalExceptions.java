package in.rahul.learning.exceptions;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import in.rahul.learning.commons.BaseResponse;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptions {

    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String ERROR_PATH = "/error";
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse> handleCustomException(CustomException ex) {
        return createBaseResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * This is the exception for valid annotation for validation in entity class
     *
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Collect validation errors
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Create a structured response
        BaseResponse response = BaseResponse.builder()
                .status(400)
                .message("Validation failed")
                .data(errors)
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * This is the exception for invalid role
     *
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse> handleInvalidEnum(
            HttpMessageNotReadableException ex) {

        return createBaseResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {

        return createBaseResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<BaseResponse> createResourceAccessException(ResourceAccessException ex) {
        return createBaseResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createBaseResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> exception(Exception exception) {
        log.error(exception.getMessage(), exception);
        return createBaseResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse> runtimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return createBaseResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }


    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<BaseResponse> notFoundException(NoResultException exception) {
        log.error(exception.getMessage());
        return createBaseResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<BaseResponse> iOException(IOException exception) {
        log.error(exception.getMessage());
        return createBaseResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> illegalArgumentException(IllegalArgumentException exception) {
        log.error(exception.getMessage());
        return createBaseResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse> accessDeniedException() {
        return createBaseResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse> badCredentialsException() {
        return createBaseResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<BaseResponse> lockedException() {
        return createBaseResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<BaseResponse> tokenExpiredException(TokenExpiredException exception) {
        return createBaseResponse(INTERNAL_SERVER_ERROR, exception.getMessage());

    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<BaseResponse> signatureVerificationException(SignatureVerificationException exception) {
        log.error(exception.getMessage());
        return createBaseResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse> noResourceFoundException(NoResourceFoundException internalServerException) {
        return createBaseResponse(INTERNAL_SERVER_ERROR, internalServerException.getMessage());
    }

    private ResponseEntity<BaseResponse> createBaseResponse(HttpStatus httpStatus, String message) {
        BaseResponse baseResponse = BaseResponse.builder()
                .status(httpStatus.value())
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(baseResponse, httpStatus);
    }
}
