package ua.edu.ukma.springers.rezflix.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.ErrorResponseDto;
import ua.edu.ukma.springers.rezflix.logging.Markers;
import ua.edu.ukma.springers.rezflix.security.*;
import ua.edu.ukma.springers.rezflix.utils.MessageResolver;

import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static ua.edu.ukma.springers.rezflix.domain.enums.UserRole.*;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({JWTTokenProperties.class, SuperAdminProperties.class})
public class WebSecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger("SecurityLogger");

    public static final String LOGIN_URL = "/auth/login";
    public static final String REFRESH_URL = "/auth/refresh";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Value("${springdoc.swagger-ui.path}") String swaggerUiPath,
                                           AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint,
                                           AccessDeniedHandler accessDeniedHandler, JWTService jwtService, UserDetailsService userDetailsService,
                                           ObjectMapper objectMapper) throws Exception
    {
        return http
                .cors(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(c ->
                        c.authenticationEntryPoint(authenticationEntryPoint)
                         .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterAfter(new LoginFilter(LOGIN_URL, objectMapper, jwtService, authenticationManager), ExceptionTranslationFilter.class)
                .addFilterAfter(new RefreshFilter(REFRESH_URL, objectMapper, jwtService, userDetailsService), LoginFilter.class)
                .addFilterAfter(new JWTAuthenticationFilter(authenticationManager), RefreshFilter.class)
                .authorizeHttpRequests(r ->
                    r
                     // swagger
                     .requestMatchers(GET, swaggerUiPath, "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs*/**").permitAll()
                     // auth-api
                     .requestMatchers(POST, LOGIN_URL, REFRESH_URL).permitAll()
                     // user-api
                     .requestMatchers(POST, "/api/user").hasAuthority(SUPER_ADMIN.name())
                     .requestMatchers("/api/user", "/api/user/*").permitAll()
                     // film-content-api
                     .requestMatchers(GET, "/api/film/*/content").permitAll()
                     .requestMatchers("/api/film/*/content").hasAuthority(CONTENT_MANAGER.name())
                     // film-rating-api
                     .requestMatchers("/api/film/*/rating").hasAuthority(VIEWER.name())
                     // film-comment-api
                     .requestMatchers(GET, "/api/film/comment", "/api/film/comment/*").permitAll()
                     .requestMatchers(POST, "/api/film/comment").hasAuthority(VIEWER.name())
                     .requestMatchers("/api/film/comment/*").hasAnyAuthority(VIEWER.name(), MODERATOR.name())
                     // film-collection-api
                     .requestMatchers(POST, "/api/film-collections").hasAnyAuthority(VIEWER.name())
                     .requestMatchers("/api/film-collections", "/api/film-collections/*").hasAnyAuthority(VIEWER.name(), MODERATOR.name())
                     // film-info-lookup-api
                     .requestMatchers(GET, "/api/film/info-lookup").hasAuthority(CONTENT_MANAGER.name())
                     // film-api
                     .requestMatchers(GET, "/api/film", "/api/film/*").permitAll()
                     .requestMatchers("/api/film", "/api/film/*").hasAuthority(CONTENT_MANAGER.name())
                     // cache-api
                     .requestMatchers("/api/cache/*").hasAuthority(SUPER_ADMIN.name())
                     // deny other requests
                     .requestMatchers("/**").denyAll()
                )
                .build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper, MessageResolver messageResolver) {
        return (request, response, authException) -> {
            LOGGER.warn(Markers.EXCEPTION, "Authentication failure from host {}: {}", request.getRemoteHost(), authException.getMessage());
            ErrorResponseDto errorResponseBody = new ErrorResponseDto(messageResolver.resolve("error.application.unauthenticated"), List.of());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), errorResponseBody);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(SecurityUtils securityUtils, ObjectMapper objectMapper, MessageResolver messageResolver) {
        return (request, response, authException) -> {
            LOGGER.warn(Markers.EXCEPTION, "Authorization failure from user {}: {}", securityUtils.getCurrentUserId(), authException.getMessage());
            ErrorResponseDto errorResponseBody = new ErrorResponseDto(messageResolver.resolve("error.application.forbidden"), List.of());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), errorResponseBody);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(JWTService jwtService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        JWTAuthenticationProvider jwtAuthenticationProvider = new JWTAuthenticationProvider(jwtService);
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(jwtAuthenticationProvider, daoAuthenticationProvider));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${security.cors.frontend-origin}") String allowedOrigin) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(allowedOrigin);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
