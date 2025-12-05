package haru.pharmacy.config;

import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessConstraintException.class)
    public ResponseEntity<String> handleConstraintException(BusinessConstraintException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        System.err.println("Internal Server Error: " + ex.getMessage());
        return new ResponseEntity<>("Произошла внутренняя ошибка сервера.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        String translatedMessage;
        try {
            translatedMessage = getMessage(ex.getMessage());
        } catch (Exception e) {
            translatedMessage = ex.getMessage();
        }
        return new ResponseEntity<>(translatedMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessConstraintException.class)
    public ResponseEntity<String> handleBusiness(BusinessConstraintException ex) {
        return new ResponseEntity<>(getMessage(ex.getMessage(), ex.getArgs()), HttpStatus.BAD_REQUEST);
    }
}