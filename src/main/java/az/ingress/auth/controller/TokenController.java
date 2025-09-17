package az.ingress.auth.controller;

import static az.ingress.auth.model.constants.HeaderConstants.USER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.service.abstraction.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/token")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("refresh")
    public AuthResponse refreshToken(@RequestHeader(AUTHORIZATION) String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }

    @PostMapping("verify")
    public ResponseEntity<Void> verifyToken(@RequestHeader(AUTHORIZATION) String accessToken) {
        var authPayloadDto = tokenService.verifyToken(accessToken);
        return ResponseEntity.noContent()
                             .header(USER_ID, authPayloadDto.getUserId())
                             .build();
    }
}