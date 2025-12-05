package haru.pharmacy.exception;

import lombok.Getter;

@Getter
public class BusinessConstraintException extends RuntimeException {
    private final Object[] args;

    public BusinessConstraintException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}
