package az.ingress.auth.util;

import static az.ingress.auth.exception.ErrorMessage.INCORRECT_PASSWORD;

import az.ingress.auth.exception.AuthException;

public enum AuthUtil {
    AUTH_UTIL;

    public void verifyPassword(String actualPassword, String expectedPassword) {
        if (!actualPassword.equals(expectedPassword)) {
            throw new AuthException(INCORRECT_PASSWORD.getMessage(), 401);
        }
    }
}