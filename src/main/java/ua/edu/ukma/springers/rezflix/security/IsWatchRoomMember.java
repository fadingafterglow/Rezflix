package ua.edu.ukma.springers.rezflix.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import ua.edu.ukma.springers.rezflix.utils.UUIDUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class IsWatchRoomMember implements AuthorizationManager<MessageAuthorizationContext<?>> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MessageAuthorizationContext<?> context) {
        if (authentication.get() instanceof WatchRoomAuthentication watchRoomAuth) {
            UUID roomId = UUIDUtils.fromStringOrNull(context.getVariables().get("roomId"));
            return new AuthorizationDecision(Objects.equals(roomId, watchRoomAuth.getRoomId()));
        }
        return new AuthorizationDecision(false);
    }
}
