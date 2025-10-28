package ua.edu.ukma.springers.rezflix.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@RequiredArgsConstructor
public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof JWTAuthenticationToken jwtAuthenticationToken) {
            try {
                JWTService.VerificationResult verificationResult = jwtService.verifyAccessToken(jwtAuthenticationToken.getToken());
                return UsernamePasswordAuthenticationToken.authenticated(verificationResult.username(), null, List.of(new SimpleGrantedAuthority(verificationResult.role())));
            } catch (JWTVerificationException e) { /**/ }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
