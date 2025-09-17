package az.ingress.auth.service.abstraction;

import az.ingress.auth.model.dto.AuthResponse;
import az.ingress.auth.model.request.AuthRequest;

public interface AuthService {

    AuthResponse signIn(AuthRequest authRequest);
}