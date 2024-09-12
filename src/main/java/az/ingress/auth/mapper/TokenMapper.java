package az.ingress.auth.mapper;

import az.ingress.auth.model.jwt.AccessTokenClaimsSet;
import az.ingress.auth.model.jwt.RefreshTokenClaimsSet;

import java.util.Date;

import static az.ingress.auth.model.constants.AuthConstants.ISSUER;

public enum TokenMapper {
    TOKEN_MAPPER;

    public AccessTokenClaimsSet buildAccessTokenClaimsSet(String userId, Date expirationTime) {
        return AccessTokenClaimsSet.builder()
                .iss(ISSUER)
                .userId(userId)
                .createdTime(new Date())
                .expirationTime(expirationTime)
                .build();
    }

    public RefreshTokenClaimsSet buildRefreshTokenClaimsSet(String userId, int refreshTokenExpirationCount, Date expirationTime) {
        return RefreshTokenClaimsSet.builder()
                .iss(ISSUER)
                .userId(userId)
                .count(refreshTokenExpirationCount)
                .exp(expirationTime)
                .build();
    }
}