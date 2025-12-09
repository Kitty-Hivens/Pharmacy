package haru.pharmacy.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Object[] args;

    public ResourceNotFoundException(String message) {
        super(message);
        this.args = null;
    }

    public ResourceNotFoundException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}
