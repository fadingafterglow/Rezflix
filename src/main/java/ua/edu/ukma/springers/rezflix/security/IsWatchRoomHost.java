package ua.edu.ukma.springers.rezflix.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import ua.edu.ukma.springers.rezflix.domain.enums.WatchRoomUserRole;
import ua.edu.ukma.springers.rezflix.utils.UUIDUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class IsWatchRoomHost implements AuthorizationManager<MessageAuthorizationContext<?>> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MessageAuthorizationContext<?> context) {
        if (authentication.get() instanceof WatchRoomAuthentication watchRoomAuth) {
            if (watchRoomAuth.getRole() != WatchRoomUserRole.HOST)
                return new AuthorizationDecision(false);
            UUID roomId = UUIDUtils.fromStringOrNull(context.getVariables().get("roomId"));
            return new AuthorizationDecision(Objects.equals(roomId, watchRoomAuth.getRoomId()));
        }
        return new AuthorizationDecision(false);
    }
}
