package ua.edu.ukma.springers.rezflix.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONLayout extends LayoutBase<ILoggingEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String doLayout(ILoggingEvent event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("timestamp", event.getTimeStamp());
        eventMap.put("level", event.getLevel().levelStr);
        eventMap.put("thread", event.getThreadName());
        eventMap.put("logger", event.getLoggerName());
        eventMap.put("message", event.getFormattedMessage());
        eventMap.put("mdc", event.getMDCPropertyMap());
        eventMap.put("markers", mapMarkers(event));
        try {
            return objectMapper.writeValueAsString(eventMap) + "\n";
        } catch (JsonProcessingException e) {
            addError("Failed to serialize logging event", e);
            return "{}";
        }
    }

    private List<String> mapMarkers(ILoggingEvent event) {
        return event.getMarkerList() == null ? List.of() : event.getMarkerList().stream().map(Marker::getName).toList();
    }
}
