package ua.edu.ukma.springers.rezflix.controllers.websocket.model;

import lombok.*;

import java.net.URI;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WatchRoomDto extends WatchRoomStateDto {
    private final int hostUserId;
    private final UUID episodeId;
    private final URI hlsLink;

    public WatchRoomDto(boolean isPaused, long episodePositionMs, int hostUserId, UUID episodeId, URI hlsLink) {
        super(isPaused, episodePositionMs);
        this.hostUserId = hostUserId;
        this.episodeId = episodeId;
        this.hlsLink = hlsLink;
    }
}
