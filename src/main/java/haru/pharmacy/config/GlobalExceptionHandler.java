package haru.pharmacy.config;

import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

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
}