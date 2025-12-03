package ua.edu.ukma.springers.rezflix.controllers.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WatchRoomStateDto {
    @JsonProperty("paused")
    private final boolean isPaused;
    private final long episodePositionMs;
}
