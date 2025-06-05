package com.ecommerce.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // Import this
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource Not Found: {}", ex.getMessage());
        // For ResourceNotFound, we can still include details if it helps debugging, or remove it as per requirements.
        // Keeping previous ErrorDetails structure for now, but can be changed to ErrorApiResponse if preferred.
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation Error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handle IllegalArgumentException specifically for Registration (username/email already taken)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorApiResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Illegal Argument Exception: {}", ex.getMessage());
        // Return only message and status for registration specific errors
        ErrorApiResponse errorResponse = new ErrorApiResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle BadCredentialsException for Login failures
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorApiResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        logger.error("Authentication failed: {}", ex.getMessage());
        // Return a generic message for security reasons, without exposing specifics
        ErrorApiResponse errorResponse = new ErrorApiResponse(
                "Invalid username or password.", // Custom message
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Internal Server Error: {}", ex.getMessage(), ex);
        // For generic exceptions, we can still include details if it helps debugging.
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Existing ErrorDetails class for other types of errors if needed (e.g., ResourceNotFound)
    // If you want all errors to conform to ErrorApiResponse, you can remove this and adjust other handlers.
    // For this request, we keep it for non-specific cases or cases where details are still desired.
    // Make sure to remove this if you want all errors to use ErrorApiResponse.
    // For now, I'm keeping it separate as per the specific request to change ONLY register and login errors.
    private static class ErrorDetails {
        private LocalDateTime timestamp;
        private String message;
        private String details; // This will still exist for other exceptions
        public ErrorDetails(LocalDateTime timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getMessage() { return message; }
        public String getDetails() { return details; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public void setMessage(String message) { this.message = message; }
        public void setDetails(String details) { this.details = details; }
    }
}