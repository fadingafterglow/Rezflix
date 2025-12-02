package ua.edu.ukma.springers.rezflix.controllers.websocket.model;

import lombok.Data;

@Data
public class ChatMessageDto {
    private final String username;
    private final String message;
}
