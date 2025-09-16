package ua.edu.ukma.springers.rezflix.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.time.*;

@UtilityClass
public class TimeUtils {

    public long getCurrentTimeUTC() {
        return Instant.now().toEpochMilli();
    }

    public LocalDateTime getCurrentDateTimeUTC() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public LocalDateTime mapToUtcDateTime(@Nullable final OffsetDateTime offsetDateTime) {
        return offsetDateTime == null
                ? null
                : offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public LocalDateTime mapToCurrentTimeZone(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null
                ? null
                : wrapToUtcTimeZone(localDateTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public OffsetDateTime wrapToUtcTimeZone(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null
                ? null
                : localDateTime.atOffset(ZoneOffset.UTC);
    }
}
