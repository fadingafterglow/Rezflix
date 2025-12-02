package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.WatchRoomControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.ChatMessageDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.security.WatchRoomAuthentication;
import ua.edu.ukma.springers.rezflix.services.WatchRoomService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WatchRoomController implements WatchRoomControllerApi {

    private final WatchRoomService service;

    @Override
    public ResponseEntity<UUID> createWatchRoom(CreateWatchRoomDto createWatchRoomDto) {
        log.info("Create watch room for episode {}", createWatchRoomDto.getEpisodeId());
        return ResponseEntity.ok(service.createWatchRoom(createWatchRoomDto));
    }

    @SubscribeMapping("/watch-room/{roomId}/init")
    public WatchRoomDto getRoomInfo(@DestinationVariable UUID roomId) {
        return service.getWatchRoomInfo(roomId);
    }

    @MessageMapping("/watch-room/{roomId}/sync")
    public WatchRoomStateDto syncRoomState(@DestinationVariable UUID roomId, @Payload WatchRoomStateDto watchRoomStateDto) {
        return service.syncWatchRoomState(roomId, watchRoomStateDto);
    }

    @MessageMapping("/watch-room/{roomId}/chat")
    public ChatMessageDto sendChatMessage(@Payload String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth instanceof WatchRoomAuthentication w ? w.getUsername() : null;
        return new ChatMessageDto(username, message);
    }
}
