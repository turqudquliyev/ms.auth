package az.ingress.auth.util;

import az.ingress.auth.exception.AuthException;
import az.ingress.auth.model.jwt.RefreshTokenClaimsSet;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import static az.ingress.auth.exception.ErrorMessage.USER_UNAUTHORIZED;
import static az.ingress.auth.util.MapperUtil.MAPPER_UTIL;
import static com.nimbusds.jose.JWSAlgorithm.RS256;

@Slf4j
public enum JwtUtil {
    JWT_UTIL;

    public <T> String generateToken(T tokenClaimSet, PrivateKey privateKey) {
        try {
            var claimSetJson = MAPPER_UTIL.map(tokenClaimSet);
            var signedJWT = generateSignedJWT(claimSetJson, privateKey);
            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("ActionLog.generateToken.error ", e);
            throw new AuthException(USER_UNAUTHORIZED.name(), 401);
        }
    }

    public void verifySignature(String token, PublicKey publicKey) {
        try {
            var signedJwt = parseSignedJwt(token);
            var verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
            if (!signedJwt.verify(verifier))
                throw new AuthException(USER_UNAUTHORIZED.getMessage(), 401);
        } catch (JOSEException e) {
            log.error("ActionLog.verifyToken.error ", e);
            throw new AuthException(USER_UNAUTHORIZED.getMessage(), 401);
        }
    }

    public <T> T getClaimsFromToken(String token, Class<T> clazz) {
        try {
            var jwtClaimsSet = parseJwtClaimSet(token);
            return MAPPER_UTIL.map(jwtClaimsSet.toString(), clazz);
        } catch (Exception e) {
            log.error("ActionLog.getClaimsFromAccessToken.error ", e);
            throw new AuthException(USER_UNAUTHORIZED.getMessage(), 401);
        }
    }

    public Date generateSessionExpirationTime(Integer expirationMinutes) {
        return new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1_000);
    }

    public boolean isRefreshTokenTimeExpired(RefreshTokenClaimsSet refreshTokenClaimsSet) {
        return refreshTokenClaimsSet.getExp().before(new Date());
    }

    public boolean isRefreshTokenCountExpired(RefreshTokenClaimsSet refreshTokenClaimsSet) {
        return refreshTokenClaimsSet.getCount() <= 0;
    }

    public boolean isTokenExpired(Date expirationTime) {
        return expirationTime.before(new Date());
    }

    @SneakyThrows
    private JWTClaimsSet parseJwtClaimSet(String token) {
        var signedJWT = parseSignedJwt(token);
        return signedJWT.getJWTClaimsSet();
    }

    @SneakyThrows
    private SignedJWT generateSignedJWT(String tokenClaimSetJson, PrivateKey privateKey) {
        var jwtClaimsSet = JWTClaimsSet.parse(tokenClaimSetJson);
        var header = new JWSHeader(RS256);
        var signedJWT = new SignedJWT(header, jwtClaimsSet);
        var signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);
        return signedJWT;
    }

    @SneakyThrows
    private SignedJWT parseSignedJwt(String token) {
        return SignedJWT.parse(token);
    }
}