-- ======================================================================
-- EV CHARGING STATION - INITIAL SCHEMA (aligned with current Entities)
-- ======================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ======================================================================
-- Users
-- ======================================================================
CREATE TABLE IF NOT EXISTS users (
    id            VARCHAR(36)  PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    full_name     VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    type          VARCHAR(20)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ======================================================================
-- Stations
-- ======================================================================
CREATE TABLE IF NOT EXISTS stations (
    id      VARCHAR(36) PRIMARY KEY,
    name    VARCHAR(255),
    address VARCHAR(255),
    lat     DOUBLE,
    lng     DOUBLE,
    status  VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ======================================================================
-- Charging Points
-- ======================================================================
CREATE TABLE IF NOT EXISTS charging_points (
    id           VARCHAR(36) PRIMARY KEY,
    code         VARCHAR(50),
    max_power_kw DOUBLE NOT NULL DEFAULT 0,
    online       BOOLEAN NOT NULL DEFAULT FALSE,
    station_id   VARCHAR(36),
    CONSTRAINT fk_point_station
        FOREIGN KEY (station_id) REFERENCES stations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX uq_points_code   ON charging_points(code);
CREATE INDEX        idx_points_station ON charging_points(station_id);

-- ======================================================================
-- Connectors
-- ======================================================================
CREATE TABLE IF NOT EXISTS connectors (
    id            VARCHAR(36) PRIMARY KEY,
    type          VARCHAR(20),          -- CCS / CHAdeMO / AC
    max_current_a DOUBLE NOT NULL DEFAULT 0,
    voltage_v     DOUBLE NOT NULL DEFAULT 0,
    occupied      BOOLEAN NOT NULL DEFAULT FALSE,
    qr_code       VARCHAR(100),
    point_id      VARCHAR(36),
    CONSTRAINT fk_connector_point
        FOREIGN KEY (point_id) REFERENCES charging_points(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_connectors_point ON connectors(point_id);

-- ======================================================================
-- Reservations
-- ======================================================================
CREATE TABLE IF NOT EXISTS reservations (
    id           VARCHAR(36) PRIMARY KEY,
    driver_id    VARCHAR(36) NOT NULL,
    connector_id VARCHAR(36) NOT NULL,
    start_window TIMESTAMP   NOT NULL,
    end_window   TIMESTAMP   NOT NULL,
    status       VARCHAR(20) NOT NULL,       -- PENDING/CONFIRMED/CANCELLED
    CONSTRAINT fk_reservation_driver
        FOREIGN KEY (driver_id) REFERENCES users(id),
    CONSTRAINT fk_reservation_connector
        FOREIGN KEY (connector_id) REFERENCES connectors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_reservations_driver    ON reservations(driver_id);
CREATE INDEX idx_reservations_connector ON reservations(connector_id);
CREATE INDEX idx_reservations_status    ON reservations(status);
CREATE INDEX idx_reservations_start     ON reservations(start_window);

CREATE UNIQUE INDEX uq_reservation_time
ON reservations(connector_id, start_window, end_window);

-- ======================================================================
-- Sessions (ChargingSession)
-- ======================================================================
CREATE TABLE IF NOT EXISTS sessions (
    id             VARCHAR(36) PRIMARY KEY,
    driver_id      VARCHAR(36) NOT NULL,     -- entity: driverId
    vehicle_id     VARCHAR(36) NULL,         -- entity: vehicleId (chưa FK)
    connector_id   VARCHAR(36) NOT NULL,     -- entity: connectorId
    reservation_id VARCHAR(36) NULL,         -- entity: reservationId
    status         VARCHAR(20) NOT NULL,     -- STARTED/STOPPED/FAILED
    start_time     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time       TIMESTAMP   NULL,
    kwh_delivered  DOUBLE      NOT NULL DEFAULT 0,
    energy_cost    DOUBLE      NOT NULL DEFAULT 0,
    time_cost      DOUBLE      NOT NULL DEFAULT 0,
    idle_fee       DOUBLE      NOT NULL DEFAULT 0,
    total_cost     DOUBLE      NOT NULL DEFAULT 0,
    CONSTRAINT fk_session_driver
        FOREIGN KEY (driver_id) REFERENCES users(id),
    CONSTRAINT fk_session_connector
        FOREIGN KEY (connector_id) REFERENCES connectors(id),
    CONSTRAINT fk_session_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_sessions_driver     ON sessions(driver_id);
CREATE INDEX idx_sessions_connector  ON sessions(connector_id);
CREATE INDEX idx_sessions_status     ON sessions(status);
CREATE INDEX idx_sessions_start_time ON sessions(start_time);

-- ======================================================================
-- Invoices (Invoice entity aligned)
-- ======================================================================
CREATE TABLE IF NOT EXISTS invoices (
    id           VARCHAR(36) PRIMARY KEY,
    driver_id    VARCHAR(36) NOT NULL,         -- entity: driverId
    session_id   VARCHAR(36) NULL,             -- entity: sessionId
    total_amount DOUBLE      NOT NULL,         -- entity: amount @Column(name="total_amount")
    tax_amount   DOUBLE      NOT NULL DEFAULT 0,
    currency     VARCHAR(10) NOT NULL DEFAULT 'VND',
    status       VARCHAR(20) NOT NULL,         -- DRAFT/ISSUED/PAID/VOID
    issued_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP, -- entity: issuedAt
    paid_at      TIMESTAMP   NULL,
    pdf_url      VARCHAR(255),
    CONSTRAINT fk_invoice_driver
        FOREIGN KEY (driver_id)    REFERENCES users(id),
    CONSTRAINT fk_invoice_session
        FOREIGN KEY (session_id)   REFERENCES sessions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_invoices_driver     ON invoices(driver_id);
CREATE INDEX idx_invoices_session    ON invoices(session_id);
CREATE INDEX idx_invoices_status     ON invoices(status);
CREATE INDEX idx_invoices_issued_at  ON invoices(issued_at);

-- ======================================================================
-- Payments
-- ======================================================================
CREATE TABLE IF NOT EXISTS payments (
    id           VARCHAR(36) PRIMARY KEY,
    invoice_id   VARCHAR(36) NOT NULL,
    method       VARCHAR(40) NOT NULL,     -- EWallet/Banking/InternalWallet
    status       VARCHAR(20) NOT NULL,     -- PENDING/SETTLED/FAILED
    amount       DOUBLE      NOT NULL,
    provider_ref VARCHAR(100),
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_status  ON payments(status);
CREATE INDEX idx_payments_created ON payments(created_at);

-- ======================================================================
-- Wallets
-- ======================================================================
CREATE TABLE IF NOT EXISTS wallets (
    id            VARCHAR(36) PRIMARY KEY,
    owner_user_id VARCHAR(36) NOT NULL,
    balance       DOUBLE      NOT NULL DEFAULT 0,
    currency      VARCHAR(10) NOT NULL DEFAULT 'VND',
    CONSTRAINT fk_wallet_user
        FOREIGN KEY (owner_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wallets_owner ON wallets(owner_user_id);

SET FOREIGN_KEY_CHECKS = 1;
