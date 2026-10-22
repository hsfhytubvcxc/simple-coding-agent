package dev.agent.agent;

import dev.agent.agent.config.LlmProperties;
import dev.agent.chat.service.ChatHistoryService;
import dev.agent.chat.stream.ChatEventStream;
import dev.agent.runtime.model.CommandResult;
import dev.agent.runtime.service.ApplicationRuntimeService;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodingAgentService {

    private final CodingAssistant assistant;
    private final LlmProperties properties;
    private final ChatHistoryService history;
    private final ApplicationRuntimeService runtime;
    private final Set<UUID> activeChats = ConcurrentHashMap.newKeySet();

    public boolean isRunning(UUID chatId) {
        return activeChats.contains(chatId);
    }

    public SseEmitter reply(UUID chatId, String text) throws IOException, InterruptedException {
        if (properties.apiKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Укажите LLM_API_KEY в .env и повторите docker compose up -d.");
        }
        if (!activeChats.add(chatId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Агент уже работает в этом чате");
        }

        ChatEventStream stream = new ChatEventStream();
        try {
            runtime.open(chatId);
            stream.message(history.addUserMessage(chatId, text));
            assistant.chat(chatId, text)
                    .onPartialResponse(stream::text)
                    .onIntermediateResponse(response -> saveAssistantResponse(chatId, response, stream))
                    .beforeToolExecution(execution -> {
                        var request = execution.request();
                        stream.message(history.startTool(chatId, request.id(), request.name(), request.arguments()));
                    })
                    .onToolExecuted(execution -> {
                        CommandResult result = (CommandResult) execution.resultObject();
                        stream.message(history.finishTool(chatId, execution.request().id(),
                                result.exitCode(), result.output()));
                    })
                    .onCompleteResponse(response -> {
                        saveAssistantResponse(chatId, response, stream);
                        activeChats.remove(chatId);
                        stream.complete(chatId);
                    })
                    .onError(error -> {
                        log.error("Agent failed for chat {}", chatId, error);
                        activeChats.remove(chatId);
                        history.failRunningTools(chatId, error.toString());
                        stream.error(error);
                    })
                    .start();
            return stream.emitter();
        } catch (IOException | InterruptedException | RuntimeException e) {
            activeChats.remove(chatId);
            throw e;
        }
    }

    private void saveAssistantResponse(UUID chatId, ChatResponse response, ChatEventStream stream) {
        String text = response.aiMessage().text();
        if (text != null && !text.isBlank()) {
            stream.message(history.addAssistantMessage(chatId, text));
        }
    }
}
