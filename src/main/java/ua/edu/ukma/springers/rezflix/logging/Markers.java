package ua.edu.ukma.springers.rezflix.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Markers {
    public static final Marker EXCEPTION = MarkerFactory.getMarker("EXCEPTION");
}
