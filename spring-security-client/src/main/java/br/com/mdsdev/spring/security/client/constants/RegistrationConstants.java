package br.com.mdsdev.spring.security.client.constants;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegistrationConstants {
    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String EXPIRED_TOKEN = "Expired token.";
    public static final String VALID = "Valid";
}
