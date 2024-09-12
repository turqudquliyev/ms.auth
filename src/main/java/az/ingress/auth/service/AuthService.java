package az.ingress.auth.service;

import az.ingress.auth.client.UserClient;
import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.model.request.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserClient userClient;
    private final TokenService tokenService;

    public AuthResponse signIn(AuthRequest authRequest) {
        var userResponseDto = userClient.getUserDetails(authRequest.getUsername());
        return tokenService.prepareToken(userResponseDto.id());
    }
}