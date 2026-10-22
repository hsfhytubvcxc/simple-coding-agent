package dev.agent.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(@NotBlank String text) {
}
