package ua.edu.ukma.springers.rezflix.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RefreshRequestDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.RefreshResponseDto;

import java.io.IOException;

public class RefreshFilter extends OncePerRequestFilter {

    private final RequestMatcher refreshRequestMatcher;
    private final ObjectMapper objectMapper;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    public RefreshFilter(String refreshUrl, ObjectMapper objectMapper, JWTService jwtService, UserDetailsService userDetailsService) {
        this.refreshRequestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, refreshUrl);
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException {
        try {
            String refreshToken = objectMapper.readValue(request.getInputStream(), RefreshRequestDto.class).getRefreshToken();
            JWTService.VerificationResult verified = jwtService.verifyRefreshToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(verified.username());
            String accessToken = jwtService.generateAccessToken(userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority());
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), new RefreshResponseDto(accessToken));
        } catch (DatabindException | JWTVerificationException e) {
            throw new AuthenticationException("Invalid refresh token") {};
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return !refreshRequestMatcher.matches(request);
    }
}
