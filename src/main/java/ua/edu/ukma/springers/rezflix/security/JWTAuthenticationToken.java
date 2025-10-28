package ua.edu.ukma.springers.rezflix.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
@EqualsAndHashCode(callSuper = true)
public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public JWTAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}

