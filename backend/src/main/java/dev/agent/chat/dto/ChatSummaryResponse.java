package dev.agent.chat.dto;

import dev.agent.chat.entity.ChatSession;

import java.time.Instant;
import java.util.UUID;

public record ChatSummaryResponse(UUID id, String title, Instant updatedAt, boolean running) {
    public static ChatSummaryResponse from(ChatSession chat, boolean running) {
        return new ChatSummaryResponse(chat.getId(), chat.getTitle(), chat.getUpdatedAt(), running);
    }
}
