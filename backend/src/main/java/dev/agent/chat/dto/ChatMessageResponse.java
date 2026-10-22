package dev.agent.chat.dto;

import dev.agent.chat.constants.MessageRole;
import dev.agent.chat.constants.MessageStatus;
import dev.agent.chat.entity.ChatMessage;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        MessageRole role,
        String content,
        String toolName,
        String toolArguments,
        MessageStatus status,
        Integer exitCode,
        Instant createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(message.getId(), message.getRole(), message.getContent(),
                message.getToolName(), message.getToolArguments(), message.getStatus(),
                message.getExitCode(), message.getCreatedAt());
    }
}
