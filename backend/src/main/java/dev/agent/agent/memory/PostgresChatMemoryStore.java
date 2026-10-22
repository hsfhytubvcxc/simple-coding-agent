package dev.agent.agent.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostgresChatMemoryStore implements ChatMemoryStore {
    private final JdbcTemplate jdbc;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return jdbc.queryForList("SELECT messages FROM chat_memory WHERE chat_id = ?",
                        String.class, memoryId).stream()
                .findFirst()
                .map(ChatMessageDeserializer::messagesFromJson)
                .orElseGet(List::of);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        jdbc.update("""
                INSERT INTO chat_memory(chat_id, messages) VALUES (?, ?)
                ON CONFLICT (chat_id) DO UPDATE SET messages = EXCLUDED.messages
                """, memoryId, ChatMessageSerializer.messagesToJson(messages));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        jdbc.update("DELETE FROM chat_memory WHERE chat_id = ?", memoryId);
    }
}
