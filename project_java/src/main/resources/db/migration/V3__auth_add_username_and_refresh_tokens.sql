-- Add username column (unique). Giả định bảng users trống hoặc có thể set NOT NULL.
ALTER TABLE users
  ADD COLUMN username VARCHAR(50) NOT NULL AFTER email;

CREATE UNIQUE INDEX uq_users_username ON users(username);

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          VARCHAR(36) PRIMARY KEY,
    user_id     VARCHAR(36) NOT NULL,
    token       VARCHAR(255) NOT NULL,
    issued_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    ip          VARCHAR(64)  NULL,
    user_agent  VARCHAR(255) NULL,
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_refresh_user      ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_expires   ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_revoked   ON refresh_tokens(revoked);
CREATE UNIQUE INDEX uq_refresh_tok ON refresh_tokens(token);
