package az.ingress.auth.configuration.property;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("token.expiration")
public class TokenExpirationProperties {

    @NotNull
    private Integer accessTokenMinutes;

    @NotNull
    private Integer refreshTokenMinutes;

    @NotNull
    private Integer refreshTokenCount;
}