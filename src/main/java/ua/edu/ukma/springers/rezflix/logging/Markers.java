package ua.edu.ukma.springers.rezflix.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface Markers {
    Marker LOCAL = MarkerFactory.getMarker("LOCAL");
    Marker EXCEPTION = MarkerFactory.getMarker("EXCEPTION");
}
