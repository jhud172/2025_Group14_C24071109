CREATE TABLE chat_folders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    color_hex VARCHAR(10) NOT NULL,
    icon_key VARCHAR(40) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_chat_folders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE chat_threads (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    folder_id BIGINT,
    title VARCHAR(160) NOT NULL,
    color_hex VARCHAR(10) NOT NULL,
    icon_key VARCHAR(40) NOT NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    custom_instructions TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_chat_threads_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_threads_folder FOREIGN KEY (folder_id) REFERENCES chat_folders(id) ON DELETE SET NULL
);

CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    thread_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_chat_messages_thread FOREIGN KEY (thread_id) REFERENCES chat_threads(id) ON DELETE CASCADE
);

CREATE INDEX idx_chat_folders_user ON chat_folders (user_id);
CREATE INDEX idx_chat_threads_user ON chat_threads (user_id);
CREATE INDEX idx_chat_threads_folder ON chat_threads (folder_id);
CREATE INDEX idx_chat_messages_thread ON chat_messages (thread_id);
