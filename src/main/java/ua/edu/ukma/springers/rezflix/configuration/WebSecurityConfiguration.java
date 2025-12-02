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
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
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
                     // film-rating-api
                     .requestMatchers("/api/film/*/rating").hasAuthority(VIEWER.name())
                     // film-dubbing-api
                     .requestMatchers(GET, "/api/film/dubbing", "/api/film/dubbing/*").permitAll()
                     .requestMatchers("/api/film/dubbing", "/api/film/dubbing/*").hasAuthority(CONTENT_MANAGER.name())
                     // film-episode-api
                     .requestMatchers(GET, "/api/film/dubbing/*/episodes", "/api/film/episode/*").permitAll()
                     .requestMatchers("/api/film/dubbing/*/episodes", "/api/film/episode/*").hasAuthority(CONTENT_MANAGER.name())
                     // film-comment-api
                     .requestMatchers(GET, "/api/film/comment", "/api/film/comment/*").permitAll()
                     .requestMatchers(POST, "/api/film/comment").hasAuthority(VIEWER.name())
                     .requestMatchers("/api/film/comment/*").hasAnyAuthority(VIEWER.name(), MODERATOR.name())
                     // film-collection-api
                     .requestMatchers(POST, "/api/film-collections").hasAuthority(VIEWER.name())
                     .requestMatchers("/api/film-collections", "/api/film-collections/*").hasAnyAuthority(VIEWER.name(), MODERATOR.name())
                     // film-recommendations-api
                     .requestMatchers(GET, "/api/film/recommendations").hasAuthority(VIEWER.name())
                     // film-api
                     .requestMatchers(GET, "/api/film", "/api/film/*").permitAll()
                     .requestMatchers("/api/film", "/api/film/*").hasAuthority(CONTENT_MANAGER.name())
                     // cache-api
                     .requestMatchers("/api/cache/*").hasAuthority(SUPER_ADMIN.name())
                     // file-api
                     .requestMatchers(GET, "/api/file", "/api/file/*").permitAll()
                     .requestMatchers("/api/file", "/api/file/*").authenticated()
                     // watch-room-api
                     .requestMatchers(POST, "/api/watch-room").hasAuthority(VIEWER.name())
                     // video resources
                     .requestMatchers(GET, "/video/**").permitAll()
                     // deny other requests
                     .requestMatchers("/**").denyAll()
                )
                .build();
    }

    @Bean
    @SuppressWarnings("java:S1452")
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder builder = MessageMatcherDelegatingAuthorizationManager.builder();
        var isWatchRoomHost = new IsWatchRoomHost();
        var isWatchRoomMember = new IsWatchRoomMember();
        return builder
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT).permitAll()
                .simpSubscribeDestMatchers("/rezflix/watch-room/{roomId}/init").access(isWatchRoomMember)
                .simpMessageDestMatchers("/rezflix/watch-room/{roomId}/sync").access(isWatchRoomHost)
                .simpMessageDestMatchers("/rezflix/watch-room/{roomId}/chat").access(isWatchRoomMember)
                .simpSubscribeDestMatchers("/topic/watch-room/{roomId}/*").access(isWatchRoomMember)
                .anyMessage().denyAll()
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
    public CorsConfigurationSource corsConfigurationSource(@Value("${security.cors.frontend-origins:}") List<String> allowedOrigins) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
