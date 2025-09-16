package ua.edu.ukma.springers.rezflix.mappers;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class DefaultTypesMapper {

    @Nullable
    public LocalDateTime mapToUtcTime(@Nullable final OffsetDateTime offsetDateTime) {
        return TimeUtils.mapToUtcDateTime(offsetDateTime);
    }

    @Nullable
    public OffsetDateTime wrapToUtcTimeZone(@Nullable final LocalDateTime localDateTime) {
        return TimeUtils.wrapToUtcTimeZone(localDateTime);
    }
}
