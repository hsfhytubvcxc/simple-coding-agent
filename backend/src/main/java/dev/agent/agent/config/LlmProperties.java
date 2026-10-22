package dev.agent.agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("llm")
public record LlmProperties(
        String apiKey,
        String baseUrl,
        String model,
        Duration timeout,
        int memoryMessages
) {
}
