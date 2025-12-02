package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WatchRoomMerger implements IMerger<WatchRoomEntity, CreateWatchRoomDto, WatchRoomStateDto> {

    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;
    @Value("${watch-room.ttl}")
    private final Duration ttl;

    @Override
    public void mergeForCreate(WatchRoomEntity entity, CreateWatchRoomDto view) {
        entity.setEpisodeId(UUID.randomUUID());
        entity.setHostUserId(securityUtils.getCurrentUserId());
        entity.setPasswordHash(view.getPassword() != null ? passwordEncoder.encode(view.getPassword()) : null);
        entity.setEpisodeId(view.getEpisodeId());
        entity.setPaused(true);
        entity.setEpisodePositionMs(0);
        entity.setTtlMinutes(ttl.toMinutes());
    }

    @Override
    public void mergeForUpdate(WatchRoomEntity entity, WatchRoomStateDto view) {
        entity.setPaused(view.isPaused());
        entity.setEpisodePositionMs(view.getEpisodePositionMs());
        entity.setTtlMinutes(ttl.toMinutes());
    }
}
