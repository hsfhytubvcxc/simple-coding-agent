package dev.agent.chat.service;

import dev.agent.agent.CodingAgentService;
import dev.agent.chat.dto.ChatDetailResponse;
import dev.agent.chat.dto.ChatSummaryResponse;
import dev.agent.chat.entity.ChatSession;
import dev.agent.chat.repository.ChatSessionRepository;
import dev.agent.runtime.service.ApplicationRuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatSessionRepository chats;
    private final ChatHistoryService history;
    private final ApplicationRuntimeService runtime;
    private final CodingAgentService agent;

    public List<ChatSummaryResponse> list() {
        return chats.findAllByOrderByUpdatedAtDesc().stream()
                .map(chat -> ChatSummaryResponse.from(chat, agent.isRunning(chat.getId()))).toList();
    }

    public ChatDetailResponse create() throws IOException, InterruptedException {
        ChatSession chat = new ChatSession();
        chat.setTitle("Новое приложение");
        Instant now = Instant.now();
        chat.setCreatedAt(now);
        chat.setUpdatedAt(now);
        return get(chats.save(chat).getId());
    }

    public ChatDetailResponse get(UUID id) throws IOException, InterruptedException {
        ChatSession chat = find(id);
        return new ChatDetailResponse(chat.getId(), chat.getTitle(), runtime.open(id),
                agent.isRunning(id), history.list(id));
    }

    public SseEmitter send(UUID id, String text) throws IOException, InterruptedException {
        find(id);
        return agent.reply(id, text);
    }

    public void delete(UUID id) throws IOException, InterruptedException {
        find(id);
        if (agent.isRunning(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Дождитесь завершения работы агента");
        }
        runtime.delete(id);
        chats.deleteById(id);
    }

    private ChatSession find(UUID id) {
        return chats.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Чат не найден"));
    }
}
