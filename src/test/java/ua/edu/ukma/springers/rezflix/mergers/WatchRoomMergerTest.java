package ua.edu.ukma.springers.rezflix.mergers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateWatchRoomDto;
import ua.edu.ukma.springers.rezflix.controllers.websocket.model.WatchRoomStateDto;
import ua.edu.ukma.springers.rezflix.domain.entities.WatchRoomEntity;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchRoomMergerTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private PasswordEncoder passwordEncoder;
    private WatchRoomMerger merger;
    private final Duration ttl = Duration.ofMinutes(30);

    @BeforeEach
    void setUp() {
        merger = new WatchRoomMerger(securityUtils, passwordEncoder, ttl);
    }

    @Test
    void mergeForCreate() {
        WatchRoomEntity entity = new WatchRoomEntity();
        CreateWatchRoomDto dto = new CreateWatchRoomDto();
        dto.setEpisodeId(UUID.randomUUID());
        dto.setPassword("secret");

        when(securityUtils.getCurrentUserId()).thenReturn(5);
        when(passwordEncoder.encode("secret")).thenReturn("hashed_secret");

        merger.mergeForCreate(entity, dto);

        assertEquals(dto.getEpisodeId(), entity.getEpisodeId());
        assertEquals(5, entity.getHostUserId());
        assertEquals("hashed_secret", entity.getPasswordHash());
        assertTrue(entity.isPaused());
        assertEquals(0, entity.getEpisodePositionMs());
        assertEquals(30, entity.getTtlMinutes());
    }

    @Test
    void mergeForUpdate() {
        WatchRoomEntity entity = new WatchRoomEntity();
        WatchRoomStateDto dto = new WatchRoomStateDto(false, 5000);

        merger.mergeForUpdate(entity, dto);

        assertFalse(entity.isPaused());
        assertEquals(5000, entity.getEpisodePositionMs());
        assertEquals(30, entity.getTtlMinutes());
    }
}