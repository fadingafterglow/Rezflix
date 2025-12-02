package ua.edu.ukma.springers.rezflix.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.WatchRoomUserRole;
import ua.edu.ukma.springers.rezflix.exceptions.UnauthenticatedException;
import ua.edu.ukma.springers.rezflix.repositories.UserRepository;
import ua.edu.ukma.springers.rezflix.repositories.WatchRoomRepository;
import ua.edu.ukma.springers.rezflix.utils.UUIDUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WatchRoomAuthenticationInterceptor implements ChannelInterceptor {

    public static final String AUTHENTICATION_HEADER = "Authentication";
    public static final String ROOM_ID_HEADER = "Room-Id";
    public static final String ROOM_PASSWORD_HEADER = "Room-Password";

    private final WatchRoomRepository watchRoomRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwt = accessor.getFirstNativeHeader(AUTHENTICATION_HEADER);
            UUID roomId = UUIDUtils.fromStringOrNull(accessor.getFirstNativeHeader(ROOM_ID_HEADER));
            String roomPassword = accessor.getFirstNativeHeader(ROOM_PASSWORD_HEADER);

            String username = tryAuthenticate(jwt);
            WatchRoomEntity connected = tryConnectToRoom(roomId, roomPassword);
            if (connected != null) {
                WatchRoomUserRole role = determineUserRole(connected, username);
                WatchRoomAuthentication authentication = new WatchRoomAuthentication(username, role, connected.getRoomId());
                accessor.setUser(authentication);
                return message;
            }
            throw new UnauthenticatedException();
        }
        return message;
    }

    private String tryAuthenticate(String jwt) {
        if (jwt == null) return null;
        try {
            JWTService.VerificationResult verificationResult = jwtService.verifyAccessToken(jwt);
            return verificationResult.username();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private WatchRoomEntity tryConnectToRoom(UUID roomId, String roomPassword) {
        if (roomId == null) return null;
        WatchRoomEntity room = watchRoomRepository.findById(roomId).orElse(null);
        if (room == null) return null;
        if (room.getPasswordHash() == null) return room;
        if (roomPassword != null && passwordEncoder.matches(roomPassword, room.getPasswordHash())) return room;
        return null;
    }

    private WatchRoomUserRole determineUserRole(WatchRoomEntity room, String username) {
        boolean isHost = Optional.ofNullable(username)
                .flatMap(userRepository::findIdByUsername)
                .map(id -> id == room.getHostUserId())
                .orElse(false);
        return isHost ? WatchRoomUserRole.HOST : WatchRoomUserRole.GUEST;
    }
}
