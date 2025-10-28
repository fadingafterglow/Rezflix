package ua.edu.ukma.springers.rezflix.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.utils.TimeUtils;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final JWTTokenProperties jwtTokenProperties;

    public String generateAccessToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(TimeUtils.getCurrentTimeUTC()))
                .withExpiresAt(new Date(TimeUtils.getCurrentTimeUTC() + jwtTokenProperties.getAccessExpiration().toMillis()))
                .withClaim(JWTTokenProperties.ROLE_CLAIM, role)
                .sign(HMAC512(jwtTokenProperties.getSecret().getBytes()));
    }

    public VerificationResult verifyAccessToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtTokenProperties.getSecret().getBytes()))
                .withClaimPresence(JWTTokenProperties.ROLE_CLAIM)
                .build()
                .verify(removePrefix(token));
        return new VerificationResult(jwt.getSubject(), jwt.getClaim(JWTTokenProperties.ROLE_CLAIM).asString());
    }

    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(TimeUtils.getCurrentTimeUTC()))
                .withExpiresAt(new Date(TimeUtils.getCurrentTimeUTC() + jwtTokenProperties.getRefreshExpiration().toMillis()))
                .sign(HMAC512(jwtTokenProperties.getSecret().getBytes()));
    }

    public VerificationResult verifyRefreshToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtTokenProperties.getSecret().getBytes()))
                .build()
                .verify(removePrefix(token));
        return new VerificationResult(jwt.getSubject(), null);
    }

    private String removePrefix(String token) {
        return token.replace(JWTTokenProperties.PREFIX, "");
    }

    public record VerificationResult(String username, String role) {}
}
