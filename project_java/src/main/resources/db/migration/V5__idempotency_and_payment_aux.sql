CREATE TABLE IF NOT EXISTS idempotency_keys (
  id            VARCHAR(36) PRIMARY KEY,
  key_value     VARCHAR(80) NOT NULL,
  endpoint      VARCHAR(120) NOT NULL,
  request_hash  VARCHAR(64) NOT NULL,
  response_body TEXT NULL,
  status_code   INT NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_idem_key (key_value, endpoint)
);

CREATE TABLE IF NOT EXISTS payment_ipn_logs (
  id          VARCHAR(36) PRIMARY KEY,
  provider    VARCHAR(20) NOT NULL,
  raw_query   TEXT NOT NULL,
  verified    BOOLEAN NOT NULL DEFAULT FALSE,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
