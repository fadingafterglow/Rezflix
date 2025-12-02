package ua.edu.ukma.springers.rezflix.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ua.edu.ukma.springers.rezflix.security.WatchRoomAuthenticationInterceptor;
import ua.edu.ukma.springers.rezflix.utils.WatchRoomExceptionHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WatchRoomSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Value("${security.cors.frontend-origins}")
    private final String[] allowedOrigins;
    private final WatchRoomAuthenticationInterceptor authenticationInterceptor;
    private final ApplicationContext applicationContext;
    private final AuthorizationManager<Message<?>> authorizationManager;
    private final WatchRoomExceptionHandler exceptionHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .setErrorHandler(exceptionHandler)
                .addEndpoint("/watch-room-ws")
                .setAllowedOrigins(allowedOrigins);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config
                .setPreservePublishOrder(true)
                .setApplicationDestinationPrefixes("/rezflix")
                .enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(authorizationManager);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(applicationContext);
        authz.setAuthorizationEventPublisher(publisher);
        registration.interceptors(authenticationInterceptor, new SecurityContextChannelInterceptor(), authz);
    }
}
