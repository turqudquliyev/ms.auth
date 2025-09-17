package az.ingress.auth.exception;

import static az.ingress.auth.exception.ErrorMessage.CLIENT_ERROR;
import static az.ingress.auth.exception.ErrorMessage.UNEXPECTED_ERROR;
import static az.ingress.auth.exception.ErrorMessage.VALIDATION_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import az.ingress.auth.logger.ApplicationLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    private final String VALIDATION_ERROR_FORMAT = "%s: %s";
    private final ApplicationLogger log = ApplicationLogger.getLogger(ErrorHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception ex) {
        log.error("Exception: ", ex);
        return new ErrorResponse(UNEXPECTED_ERROR.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handle(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException: ", ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomFeignException.class)
    public ResponseEntity<ErrorResponse> handle(CustomFeignException ex) {
        log.error("CustomFeignException: ", ex);
        return ResponseEntity.status(ex.getStatus())
                             .body(new ErrorResponse(CLIENT_ERROR.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handle(AuthException ex) {
        log.error("AuthException: ", ex);
        return ResponseEntity.status(ex.getStatus())
                             .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handle(BindException ex) {
        log.error("BindException: ", ex);
        var fieldErrors = ex.getFieldErrors();
        var errorReasons = fieldErrors.stream()
                                      .map(it -> VALIDATION_ERROR_FORMAT.formatted(it.getField(), it.getDefaultMessage()))
                                      .toList();
        return new ErrorResponse(VALIDATION_ERROR.getMessage(), errorReasons);
    }
}