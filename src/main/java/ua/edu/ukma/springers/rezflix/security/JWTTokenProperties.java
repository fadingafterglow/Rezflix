package ua.edu.ukma.springers.rezflix.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security.token")
public class JWTTokenProperties {
    public static final String PREFIX = "Bearer ";
    public static final String ROLE_CLAIM = "role";

    private final String secret;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
}