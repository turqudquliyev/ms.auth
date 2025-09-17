package az.ingress.auth.exception;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(NON_EMPTY)
public class ErrorResponse {
    private String message;
    private List<String> validationErrors;

    public ErrorResponse(String message) {
        this.message = message;
        this.validationErrors = emptyList();
    }
}