-- =====================================================
-- V9__create_incident_tickets.sql
-- Bảng lưu ticket sự cố tại trạm sạc
-- =====================================================

CREATE TABLE incident_tickets (
    id VARCHAR(36) NOT NULL PRIMARY KEY,

    station_id  VARCHAR(36) COLLATE utf8mb4_0900_ai_ci NOT NULL,

    connector_id VARCHAR(36) COLLATE utf8mb4_0900_ai_ci NULL,

    severity VARCHAR(20) NOT NULL,
    status   VARCHAR(20) NOT NULL DEFAULT 'OPEN',

    description    TEXT NOT NULL,
    attachment_url VARCHAR(255),

    reported_by VARCHAR(36) COLLATE utf8mb4_0900_ai_ci NULL,
    assigned_to VARCHAR(36) COLLATE utf8mb4_0900_ai_ci NULL,

    category VARCHAR(50),
    source   VARCHAR(30),

    resolution TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL DEFAULT NULL,

    CONSTRAINT chk_incident_severity
        CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),

    CONSTRAINT chk_incident_status
        CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),

    CONSTRAINT fk_incident_station
        FOREIGN KEY (station_id)  REFERENCES stations(id),

    CONSTRAINT fk_incident_connector
        FOREIGN KEY (connector_id) REFERENCES connectors(id),

    CONSTRAINT fk_incident_reporter
        FOREIGN KEY (reported_by)  REFERENCES users(id),

    CONSTRAINT fk_incident_assignee
        FOREIGN KEY (assigned_to)  REFERENCES users(id)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX idx_incident_station   ON incident_tickets (station_id);
CREATE INDEX idx_incident_status    ON incident_tickets (status);
CREATE INDEX idx_incident_severity  ON incident_tickets (severity);
CREATE INDEX idx_incident_connector ON incident_tickets (connector_id);
