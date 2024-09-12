package az.ingress.auth.service;

import az.ingress.auth.configuration.property.TokenExpirationProperties;
import az.ingress.auth.exception.AuthException;
import az.ingress.auth.model.cache.AuthCacheData;
import az.ingress.auth.model.dto.AuthPayloadDto;
import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.model.jwt.AccessTokenClaimsSet;
import az.ingress.auth.model.jwt.RefreshTokenClaimsSet;
import az.ingress.auth.util.CacheProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static az.ingress.auth.exception.ErrorMessage.REFRESH_TOKEN_COUNT_EXPIRED;
import static az.ingress.auth.exception.ErrorMessage.TOKEN_EXPIRED;
import static az.ingress.auth.exception.ErrorMessage.UNEXPECTED_ERROR;
import static az.ingress.auth.exception.ErrorMessage.USER_UNAUTHORIZED;
import static az.ingress.auth.mapper.TokenMapper.TOKEN_MAPPER;
import static az.ingress.auth.model.constants.CacheConstants.CACHE_EXPIRE_SECONDS;
import static az.ingress.auth.util.CertificateKeyUtil.CERTIFICATE_KEY_UTIL;
import static az.ingress.auth.util.JwtUtil.JWT_UTIL;
import static jodd.util.Base64.encodeToString;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenExpirationProperties tokenExpirationProperties;
    private final CacheProvider cacheProvider;

    public AuthResponse prepareToken(String userId) {
        final var refreshTokenExpirationCount = 50;
        return generateToken(userId, refreshTokenExpirationCount);
    }

    public AuthResponse refreshToken(String refreshToken) {
        var refreshTokenClaimsSet = JWT_UTIL.getClaimsFromToken(refreshToken, RefreshTokenClaimsSet.class);
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
            throw ex;
        } catch (Exception ex) {
            throw new AuthException(USER_UNAUTHORIZED.getMessage(), 401);
        }
    }

    public AuthPayloadDto verifyToken(String accessToken) {
        var userId = JWT_UTIL.getClaimsFromToken(accessToken, AccessTokenClaimsSet.class).getUserId();

        try {
            var authCacheData = fetchFromCache(userId);

            if (authCacheData == null) throw new AuthException(TOKEN_EXPIRED.getMessage(), 406);

            var publicKey = CERTIFICATE_KEY_UTIL.getPublicKey(authCacheData.getPublicKey());

            JWT_UTIL.verifySignature(accessToken, publicKey);

            if (JWT_UTIL.isTokenExpired(authCacheData.getAccessTokenClaimsSet().getExpirationTime()))
                throw new AuthException(TOKEN_EXPIRED.getMessage(), 406);

            return AuthPayloadDto.of(userId);
        } catch (AuthException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthException(UNEXPECTED_ERROR.getMessage(), 401);
        }
    }

    private AuthResponse generateToken(String userId, int refreshTokenExpirationCount) {
        var accessTokenClaimsSet = TOKEN_MAPPER.buildAccessTokenClaimsSet(
                userId,
                JWT_UTIL.generateSessionExpirationTime(tokenExpirationProperties.getAccessToken())
        );

        var refreshTokenClaimsSet = TOKEN_MAPPER.buildRefreshTokenClaimsSet(
                userId,
                refreshTokenExpirationCount,
                JWT_UTIL.generateSessionExpirationTime(tokenExpirationProperties.getRefreshToken())
        );

        var keyPair = CERTIFICATE_KEY_UTIL.generateKeyPair();

        var authCacheData = AuthCacheData.of(
                accessTokenClaimsSet,
                encodeToString(keyPair.getPublic().getEncoded())
        );

        cacheProvider.updateToCache(authCacheData, userId, CACHE_EXPIRE_SECONDS);

        var privateKey = keyPair.getPrivate();
        var accessToken = JWT_UTIL.generateToken(accessTokenClaimsSet, privateKey);
        var refreshToken = JWT_UTIL.generateToken(refreshTokenClaimsSet, privateKey);

        return AuthResponse.of(accessToken, refreshToken);
    }

    private void verifyRefreshToken(RefreshTokenClaimsSet refreshTokenClaimsSet) {
        if (JWT_UTIL.isRefreshTokenTimeExpired(refreshTokenClaimsSet))
            throw new AuthException(TOKEN_EXPIRED.getMessage(), 401);

        if (JWT_UTIL.isRefreshTokenCountExpired(refreshTokenClaimsSet))
            throw new AuthException(REFRESH_TOKEN_COUNT_EXPIRED.getMessage(), 401);
    }

    private AuthCacheData fetchFromCache(String cacheKey) {
        return cacheProvider.getBucket(cacheKey);
    }
}