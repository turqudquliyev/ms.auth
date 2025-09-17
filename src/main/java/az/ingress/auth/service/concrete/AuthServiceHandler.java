package az.ingress.auth.service.concrete;

import static az.ingress.auth.util.AuthUtil.AUTH_UTIL;

import az.ingress.auth.client.UserClient;
import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.model.request.AuthRequest;
import az.ingress.auth.service.abstraction.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceHandler implements AuthService {
    private final UserClient userClient;
    private final TokenServiceHandler tokenService;

    public AuthResponse signIn(AuthRequest authRequest) {
        var userResponseDto = userClient.getUserDetails(authRequest.getUsername());
        AUTH_UTIL.verifyPassword(userResponseDto.password(), authRequest.getPassword());
        return tokenService.prepareToken(userResponseDto.id());
    }
}