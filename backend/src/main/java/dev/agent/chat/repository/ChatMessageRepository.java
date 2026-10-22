package dev.agent.chat.repository;

import dev.agent.chat.constants.MessageStatus;
import dev.agent.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatIdOrderByCreatedAtAsc(UUID chatId);

    Optional<ChatMessage> findByChatIdAndToolCallId(UUID chatId, String toolCallId);

    boolean existsByChatId(UUID chatId);

    List<ChatMessage> findByChatIdAndStatus(UUID chatId, MessageStatus status);
}
