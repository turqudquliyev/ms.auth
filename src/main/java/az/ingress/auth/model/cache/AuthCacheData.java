package az.ingress.auth.model.cache;

import az.ingress.auth.model.jwt.AccessTokenClaimsSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
public class AuthCacheData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private AccessTokenClaimsSet accessTokenClaimsSet;
    private String publicKey;
}