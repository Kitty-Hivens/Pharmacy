package haru.pharmacy.config;

import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * <p>
 * Intercepts exceptions thrown by controllers and services to provide
 * a consistent and localized JSON response structure.
 *
 * @author Haru
 * @version 1.0
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
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

    /**
     * Handles {@link ResourceNotFoundException}.
     * Returns HTTP 404 (Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        String message = getMessage(ex.getMessage(), ex.getArgs());
        log.warn("Resource Not Found: {}", message);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link BusinessConstraintException}.
     * Returns HTTP 400 (Bad Request).
     */
    @ExceptionHandler(BusinessConstraintException.class)
    public ResponseEntity<String> handleBusiness(BusinessConstraintException ex) {
        String message = getMessage(ex.getMessage(), ex.getArgs());
        log.warn("Business Logic Error: {}", message);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors (e.g. @NotNull, @Size).
     * Returns HTTP 400 (Bad Request) with a map of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
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
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("UNEXPECTED ERROR", ex);
        return new ResponseEntity<>("Internal Server Error. Please contact support.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles Optimistic Locking failures (concurrent updates).
     * Returns HTTP 409 (Conflict).
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic lock failure: {}", ex.getMessage());
        String message = getMessage("error.optimistic.lock");
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}
