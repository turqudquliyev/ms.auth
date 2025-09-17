package az.ingress.auth.service.abstraction;

import az.ingress.auth.model.dto.AuthPayloadResponse;
import az.ingress.auth.model.dto.AuthResponse;

public interface TokenService {

    AuthResponse prepareToken(String userId);

    AuthResponse refreshToken(String refreshToken);

    AuthPayloadResponse verifyToken(String accessToken);
}