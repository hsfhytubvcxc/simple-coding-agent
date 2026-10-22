package dev.agent.chat.stream;

import dev.agent.chat.dto.ChatMessageResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ChatEventStream {
    private final SseEmitter emitter = new SseEmitter(1_800_000L);
    private volatile boolean connected = true;

    public ChatEventStream() {
        emitter.onCompletion(() -> connected = false);
        emitter.onError(error -> connected = false);
        emitter.onTimeout(() -> {
            connected = false;
            emitter.complete();
        });
    }

    public SseEmitter emitter() {
        return emitter;
    }

    public void text(String text) {
        send("text", Map.of("text", text));
    }

    public void message(ChatMessageResponse message) {
        send("message", message);
    }

    public void complete(UUID chatId) {
        send("complete", Map.of("chatId", chatId));
        emitter.complete();
    }

    public void error(Throwable error) {
        send("error", Map.of("message", error.toString()));
        emitter.complete();
    }

    private void send(String event, Object data) {
        if (!connected) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name(event).data(data));
        } catch (IOException | IllegalStateException e) {
            connected = false;
        }
    }
}
