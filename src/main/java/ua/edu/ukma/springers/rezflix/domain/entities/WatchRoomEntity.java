package ua.edu.ukma.springers.rezflix.domain.entities;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RedisHash("watch_room")
public class WatchRoomEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID roomId;

    private int hostUserId;
    private String passwordHash;

    @NotNull(message = "error.watch_room.episode.not_existent")
    private UUID episodeId;

    private boolean isPaused;
    private long episodePositionMs;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private long ttlMinutes;
}
