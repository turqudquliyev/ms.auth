package az.ingress.auth.model.constants;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AuthConstants {
    public static final int KEY_SIZE = 2048;
    public static final String RSA = "RSA";
    public static final String ISSUER = "ms-auth";
}