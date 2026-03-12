package haru.pharmacy.config;

import haru.pharmacy.dto.ApiErrorResponse;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * <p>
 * Intercepts exceptions thrown by controllers and services to provide
 * a consistent and localized JSON response structure.
 *
 * @author Haru
 * @version 1.1
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
@SuppressWarnings({"DataFlowIssue", "NullableProblems"})
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Retrieves a localized message from the message source.
     *
     * @param key  The message key in the properties file.
     * @param args Arguments to replace placeholders in the message.
     * @return The localized message or the key if not found.
     */
    private String getMessage(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return key;
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundV2(ResourceNotFoundException ex, HttpServletRequest request) {
        String message = getMessage(ex.getMessage(), ex.getArgs());
        log.warn("Resource Not Found: {}", message);
        return buildErrorResponse(HttpStatus.NOT_FOUND, message, request.getRequestURI());
    }

    @ExceptionHandler(BusinessConstraintException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessV2(BusinessConstraintException ex, HttpServletRequest request) {
        String message = getMessage(ex.getMessage(), ex.getArgs());
        log.warn("Business Logic Error: {}", message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationV2(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Validation Failed: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + errors, request.getRequestURI());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockV2(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        log.warn("Optimistic lock failure: {}", ex.getMessage());
        String message = getMessage("error.optimistic.lock");
        return buildErrorResponse(HttpStatus.CONFLICT, message, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericV2(Exception ex, HttpServletRequest request) {
        log.error("UNEXPECTED ERROR", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error. Please contact support.", request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityV2(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data Integrity Violation: {}", ex.getMessage());
        String message = "Cannot delete this record because it is referenced in history (e.g., Sales).";
        return buildErrorResponse(HttpStatus.CONFLICT, message, request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Handles {@link ResourceNotFoundException}.
     * Returns HTTP 404 (Not Found).
     */
    @Deprecated
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        String message = getMessage(ex.getMessage(), ex.getArgs());
        log.warn("Resource Not Found: {}", message);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link BusinessConstraintException}.
     * Returns HTTP 400 (Bad Request).
     */
    @Deprecated
    public ResponseEntity<String> handleBusiness(BusinessConstraintException ex) {
        String message = getMessage(ex.getMessage(), ex.getArgs());
        log.warn("Business Logic Error: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors (e.g. @NotNull, @Size).
     * Returns HTTP 400 (Bad Request) with a map of field errors.
     */
    @Deprecated
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Validation Failed: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unexpected system exceptions.
     * Returns HTTP 500 (Internal Server Error).
     */
    @Deprecated
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("UNEXPECTED ERROR", ex);
        return new ResponseEntity<>("Internal Server Error. Please contact support.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles Optimistic Locking failures (concurrent updates).
     * Returns HTTP 409 (Conflict).
     */
    @Deprecated
    public ResponseEntity<String> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic lock failure: {}", ex.getMessage());
        String message = getMessage("error.optimistic.lock");
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}
