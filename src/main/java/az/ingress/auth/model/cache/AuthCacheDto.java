package az.ingress.auth.model.cache;

import az.ingress.auth.model.jwt.AccessTokenClaimsDto;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AuthCacheDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private AccessTokenClaimsDto accessTokenClaimsDto;
    private String publicKey;
}