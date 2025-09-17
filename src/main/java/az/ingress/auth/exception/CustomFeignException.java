package az.ingress.auth.exception;

import lombok.Getter;

@Getter
public class CustomFeignException extends RuntimeException {
    private final int status;

    public CustomFeignException(String message, int status) {
        super(message);
        this.status = status;
    }
}