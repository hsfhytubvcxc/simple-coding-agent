package dev.agent.agent.config;

import dev.agent.agent.memory.PostgresChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfiguration {
    @Bean
    StreamingChatModel streamingChatModel(LlmProperties properties) {
        return OpenAiStreamingChatModel.builder()
                .apiKey(properties.apiKey().isBlank() ? "not-configured" : properties.apiKey())
                .baseUrl(properties.baseUrl())
                .modelName(properties.model())
                .timeout(properties.timeout())
                .strictTools(true)
                .parallelToolCalls(false)
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider(PostgresChatMemoryStore store, LlmProperties properties) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(properties.memoryMessages())
                .chatMemoryStore(store)
                .build();
    }
}
