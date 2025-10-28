package ua.edu.ukma.springers.rezflix.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security.super-admin")
public class SuperAdminProperties {
    private final String login;
    private final String passwordHash;
}