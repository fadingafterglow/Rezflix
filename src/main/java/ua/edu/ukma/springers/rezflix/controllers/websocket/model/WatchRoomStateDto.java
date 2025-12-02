package ua.edu.ukma.springers.rezflix.controllers.websocket.model;

import lombok.Data;

@Data
public class WatchRoomStateDto {
    private final boolean isPaused;
    private final long episodePositionMs;
}
