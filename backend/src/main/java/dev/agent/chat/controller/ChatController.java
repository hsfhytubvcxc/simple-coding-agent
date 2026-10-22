package dev.agent.chat.controller;

import dev.agent.chat.dto.ChatDetailResponse;
import dev.agent.chat.dto.ChatSummaryResponse;
import dev.agent.chat.dto.SendMessageRequest;
import dev.agent.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chats;

    @GetMapping
    public List<ChatSummaryResponse> list() {
        return chats.list();
    }

    @PostMapping
    public ChatDetailResponse create() throws IOException, InterruptedException {
        return chats.create();
    }

    @GetMapping("/{id}")
    public ChatDetailResponse get(@PathVariable UUID id) throws IOException, InterruptedException {
        return chats.get(id);
    }

    @PostMapping(value = "/{id}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter send(@PathVariable UUID id, @Valid @RequestBody SendMessageRequest request)
            throws IOException, InterruptedException {
        return chats.send(id, request.text());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) throws IOException, InterruptedException {
        chats.delete(id);
    }
}
