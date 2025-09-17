package az.ingress.auth.service.concrete;

import static az.ingress.auth.exception.ErrorMessage.REFRESH_TOKEN_COUNT_EXPIRED;
import static az.ingress.auth.exception.ErrorMessage.TOKEN_EXPIRED;
import static az.ingress.auth.exception.ErrorMessage.UNEXPECTED_ERROR;
import static az.ingress.auth.exception.ErrorMessage.USER_UNAUTHORIZED;
import static az.ingress.auth.mapper.TokenMapper.TOKEN_MAPPER;
import static az.ingress.auth.model.constants.CacheConstants.CACHE_EXPIRE_SECONDS;
import static az.ingress.auth.util.CertificateKeyUtil.CERTIFICATE_KEY_UTIL;
import static az.ingress.auth.util.JwtUtil.JWT_UTIL;
import static jodd.util.Base64.encodeToString;

import az.ingress.auth.configuration.property.TokenExpirationProperties;
import az.ingress.auth.exception.AuthException;
import az.ingress.auth.logger.ApplicationLogger;
import az.ingress.auth.model.cache.AuthCacheDto;
import az.ingress.auth.model.dto.AuthPayloadResponse;
import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.model.jwt.AccessTokenClaimsDto;
import az.ingress.auth.model.jwt.RefreshTokenClaimsDto;
import az.ingress.auth.service.abstraction.TokenService;
import az.ingress.auth.util.RedisProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceHandler implements TokenService {
    private final ApplicationLogger log = ApplicationLogger.getLogger(TokenService.class);
    private final TokenExpirationProperties tokenExpirationProperties;
    private final RedisProvider redisProvider;

    public AuthResponse prepareToken(String userId) {
        final var refreshTokenExpirationCount = 50;
        return generateToken(userId, refreshTokenExpirationCount);
    }

    public AuthResponse refreshToken(String refreshToken) {
        var refreshTokenClaimsSet = JWT_UTIL.getClaimsFromToken(refreshToken, RefreshTokenClaimsDto.class);
        var refreshTokenExpirationCount = refreshTokenClaimsSet.getCount() - 1;
        var userId = refreshTokenClaimsSet.getUserId();

        try {
            var authCacheData = fetchFromCache(userId);

            if (authCacheData == null) throw new AuthException(USER_UNAUTHORIZED.getMessage(), 401);

            var publicKey = CERTIFICATE_KEY_UTIL.getPublicKey(authCacheData.getPublicKey());

            JWT_UTIL.verifySignature(refreshToken, publicKey);

            verifyRefreshToken(refreshTokenClaimsSet);

            return generateToken(userId, refreshTokenExpirationCount);
        } catch (AuthException ex) {
            log.error("ActionLog.refreshToken.error ", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("ActionLog.refreshToken.error ", ex);
            throw new AuthException(USER_UNAUTHORIZED.getMessage(), 401);
        }
    }

    public AuthPayloadResponse verifyToken(String accessToken) {
        var userId = JWT_UTIL.getClaimsFromToken(accessToken, AccessTokenClaimsDto.class)
                             .getUserId();

        try {
            var authCacheData = fetchFromCache(userId);

            if (authCacheData == null) throw new AuthException(TOKEN_EXPIRED.getMessage(), 406);

            var publicKey = CERTIFICATE_KEY_UTIL.getPublicKey(authCacheData.getPublicKey());

            JWT_UTIL.verifySignature(accessToken, publicKey);

            if (JWT_UTIL.isTokenExpired(authCacheData.getAccessTokenClaimsDto()
                                                     .getExpirationTime())) {throw new AuthException(TOKEN_EXPIRED.getMessage(), 406);}

            return AuthPayloadResponse.of(userId);
        } catch (AuthException ex) {
            log.error("ActionLog.verifyToken.error ", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("ActionLog.verifyToken.error ", ex);
            throw new AuthException(UNEXPECTED_ERROR.getMessage(), 401);
        }
    }

    private AuthResponse generateToken(String userId, int refreshTokenExpirationCount) {
        var accessTokenClaimsSet = TOKEN_MAPPER.buildAccessTokenClaimsSet(
                userId,
                JWT_UTIL.generateSessionExpirationTime(tokenExpirationProperties.getAccessTokenMinutes())
        );

        var refreshTokenClaimsSet = TOKEN_MAPPER.buildRefreshTokenClaimsSet(
                userId,
                refreshTokenExpirationCount,
                JWT_UTIL.generateSessionExpirationTime(tokenExpirationProperties.getRefreshTokenMinutes())
        );

        var keyPair = CERTIFICATE_KEY_UTIL.generateKeyPair();

        var authCacheData = AuthCacheDto.of(
                accessTokenClaimsSet,
                encodeToString(keyPair.getPublic().getEncoded())
        );

        redisProvider.updateToCache(authCacheData, userId, CACHE_EXPIRE_SECONDS);

        var privateKey = keyPair.getPrivate();
        var accessToken = JWT_UTIL.generateToken(accessTokenClaimsSet, privateKey);
        var refreshToken = JWT_UTIL.generateToken(refreshTokenClaimsSet, privateKey);

        return AuthResponse.of(accessToken, refreshToken);
    }

    private void verifyRefreshToken(RefreshTokenClaimsDto refreshTokenClaimsDto) {
        if (JWT_UTIL.isRefreshTokenTimeExpired(refreshTokenClaimsDto)) {throw new AuthException(TOKEN_EXPIRED.getMessage(), 401);}

        if (JWT_UTIL.isRefreshTokenCountExpired(refreshTokenClaimsDto)) {throw new AuthException(REFRESH_TOKEN_COUNT_EXPIRED.getMessage(), 401);}
    }

    private AuthCacheDto fetchFromCache(String cacheKey) {
        return redisProvider.getBucket(cacheKey);
    }
}