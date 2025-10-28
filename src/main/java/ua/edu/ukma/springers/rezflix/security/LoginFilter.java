package ua.edu.ukma.springers.rezflix.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginRequestDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.LoginResponseDto;

import java.io.IOException;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JWTService jwtService;

    public LoginFilter(String loginUrl, ObjectMapper objectMapper, JWTService jwtService, AuthenticationManager authenticationManager) {
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginUrl), authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        setAuthenticationConverter(new RequestBodyToAuthenticationConverter(objectMapper));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String role = authResult.getAuthorities().iterator().next().getAuthority();
        String accessToken = jwtService.generateAccessToken(authResult.getName(), role);
        String refreshToken = jwtService.generateRefreshToken(authResult.getName());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new LoginResponseDto(accessToken, refreshToken));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        throw failed;
    }

    @RequiredArgsConstructor
    private static class RequestBodyToAuthenticationConverter implements AuthenticationConverter {

        private final ObjectMapper objectMapper;

        @Override
        public Authentication convert(HttpServletRequest request) {
            try {
                LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
                return UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getUsername(), loginRequest.getPassword());
            } catch (IOException e) {
                throw new AuthenticationException("Invalid login request") {};
            }
        }
    }
}
