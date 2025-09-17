package az.ingress.auth.mapper;

import static az.ingress.auth.model.constants.AuthConstants.ISSUER;

import az.ingress.auth.model.jwt.AccessTokenClaimsDto;
import az.ingress.auth.model.jwt.RefreshTokenClaimsDto;
import java.util.Date;

public enum TokenMapper {
    TOKEN_MAPPER;

    public AccessTokenClaimsDto buildAccessTokenClaimsSet(String userId, Date expirationTime) {
        return AccessTokenClaimsDto.builder()
                                   .iss(ISSUER)
                                   .userId(userId)
                                   .createdTime(new Date())
                                   .expirationTime(expirationTime)
                                   .build();
    }

    public RefreshTokenClaimsDto buildRefreshTokenClaimsSet(String userId, int refreshTokenExpirationCount, Date expirationTime) {
        return RefreshTokenClaimsDto.builder()
                                    .iss(ISSUER)
                                    .userId(userId)
                                    .exp(expirationTime)
                                    .count(refreshTokenExpirationCount)
                                    .build();
    }
}