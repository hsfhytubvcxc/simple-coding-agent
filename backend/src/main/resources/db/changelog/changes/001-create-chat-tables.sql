CREATE TABLE chats (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    chat_id UUID NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    tool_call_id VARCHAR(255),
    tool_name VARCHAR(255),
    tool_arguments TEXT,
    status VARCHAR(20) NOT NULL,
    exit_code INTEGER,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_chat_messages_history ON chat_messages(chat_id, created_at);

CREATE TABLE chat_memory (
    chat_id UUID PRIMARY KEY REFERENCES chats(id) ON DELETE CASCADE,
    messages TEXT NOT NULL
);
