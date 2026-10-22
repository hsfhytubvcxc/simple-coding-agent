package dev.agent.chat.service;

import dev.agent.chat.constants.MessageRole;
import dev.agent.chat.constants.MessageStatus;
import dev.agent.chat.dto.ChatMessageResponse;
import dev.agent.chat.entity.ChatMessage;
import dev.agent.chat.entity.ChatSession;
import dev.agent.chat.repository.ChatMessageRepository;
import dev.agent.chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {
    private final ChatSessionRepository chats;
    private final ChatMessageRepository messages;

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> list(UUID chatId) {
        return messages.findByChatIdOrderByCreatedAtAsc(chatId).stream()
                .map(ChatMessageResponse::from).toList();
    }

    @Transactional
    public ChatMessageResponse addUserMessage(UUID chatId, String text) {
        ChatSession chat = chats.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Чат не найден"));
        if (!messages.existsByChatId(chatId)) {
            chat.setTitle(text.substring(0, Math.min(text.length(), 60)));
        }
        chat.setUpdatedAt(Instant.now());

        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setRole(MessageRole.USER);
        message.setContent(text);
        message.setStatus(MessageStatus.COMPLETED);
        message.setCreatedAt(Instant.now());
        return ChatMessageResponse.from(messages.save(message));
    }

    @Transactional
    public ChatMessageResponse addAssistantMessage(UUID chatId, String text) {
        ChatSession chat = chats.getReferenceById(chatId);
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setRole(MessageRole.ASSISTANT);
        message.setContent(text);
        message.setStatus(MessageStatus.COMPLETED);
        message.setCreatedAt(Instant.now());
        return ChatMessageResponse.from(messages.save(message));
    }

    @Transactional
    public ChatMessageResponse startTool(UUID chatId, String callId, String name, String arguments) {
        ChatSession chat = chats.getReferenceById(chatId);
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setRole(MessageRole.TOOL);
        message.setContent("");
        message.setToolCallId(callId);
        message.setToolName(name);
        message.setToolArguments(arguments);
        message.setStatus(MessageStatus.RUNNING);
        message.setCreatedAt(Instant.now());
        return ChatMessageResponse.from(messages.save(message));
    }

    @Transactional
    public ChatMessageResponse finishTool(UUID chatId, String callId, int exitCode, String output) {
        ChatMessage message = messages.findByChatIdAndToolCallId(chatId, callId).orElseThrow();
        message.setExitCode(exitCode);
        message.setContent(output);
        message.setStatus(exitCode == 0 ? MessageStatus.COMPLETED : MessageStatus.FAILED);
        return ChatMessageResponse.from(message);
    }

    @Transactional
    public void failRunningTools(UUID chatId, String error) {
        messages.findByChatIdAndStatus(chatId, MessageStatus.RUNNING)
                .forEach(message -> {
                    message.setExitCode(-1);
                    message.setContent(error);
                    message.setStatus(MessageStatus.FAILED);
                });
    }
}
