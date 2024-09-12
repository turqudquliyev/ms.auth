package az.ingress.auth.controller;

import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.model.request.AuthRequest;
import az.ingress.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public AuthResponse signIn(@RequestBody AuthRequest authRequest) {
        return authService.signIn(authRequest);
    }
}