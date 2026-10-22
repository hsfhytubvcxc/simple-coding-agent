package dev.agent.chat.dto;

import java.util.List;
import java.util.UUID;

public record ChatDetailResponse(
        UUID id,
        String title,
        int port,
        boolean running,
        List<ChatMessageResponse> messages
) {
}
