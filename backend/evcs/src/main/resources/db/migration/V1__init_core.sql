-- Encoding & engine
SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- ====== USERS & WALLET ======
CREATE TABLE IF NOT EXISTS users (
  id            VARCHAR(36) PRIMARY KEY,
  email         VARCHAR(120) NOT NULL UNIQUE,
  phone         VARCHAR(20),
  full_name     VARCHAR(120),
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('EV_DRIVER','CS_STAFF','ADMIN') NOT NULL,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS wallets (
  id             VARCHAR(36) PRIMARY KEY,
  owner_user_id  VARCHAR(36) NOT NULL UNIQUE,
  balance        DECIMAL(14,2) NOT NULL DEFAULT 0,
  currency       VARCHAR(8) NOT NULL,
  CONSTRAINT fk_wallet_user FOREIGN KEY (owner_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== VEHICLES ======
CREATE TABLE IF NOT EXISTS vehicles (
  id                     VARCHAR(36) PRIMARY KEY,
  driver_id              VARCHAR(36) NOT NULL,
  brand                  VARCHAR(64),
  model                  VARCHAR(64),
  plate_no               VARCHAR(32) NOT NULL,
  battery_capacity_kwh   DOUBLE,
  connector_supported    VARCHAR(32),              -- CCS/CHAdeMO/AC
  UNIQUE (plate_no),
  INDEX ix_vehicle_driver (driver_id),
  CONSTRAINT fk_vehicle_driver FOREIGN KEY (driver_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== STATIONS / CHARGING POINTS / CONNECTORS ======
CREATE TABLE IF NOT EXISTS stations (
  id       VARCHAR(36) PRIMARY KEY,
  name     VARCHAR(120) NOT NULL,
  address  VARCHAR(255),
  lat      DOUBLE NOT NULL,
  lng      DOUBLE NOT NULL,
  status   ENUM('ONLINE','OFFLINE','MAINTENANCE') NOT NULL,
  INDEX ix_station_status (status),
  INDEX ix_station_lat_lng (lat, lng)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS charging_points (
  id           VARCHAR(36) PRIMARY KEY,
  station_id   VARCHAR(36) NOT NULL,
  code         VARCHAR(64) NOT NULL,
  max_power_kw DOUBLE,
  online       BOOLEAN DEFAULT TRUE,
  UNIQUE (station_id, code),
  INDEX ix_cp_station (station_id),
  CONSTRAINT fk_cp_station FOREIGN KEY (station_id) REFERENCES stations(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS connectors (
  id                VARCHAR(36) PRIMARY KEY,
  charging_point_id VARCHAR(36) NOT NULL,
  type              ENUM('CCS','CHAdeMO','AC') NOT NULL,
  max_current_a     DOUBLE,
  voltage_v         DOUBLE,
  occupied          BOOLEAN DEFAULT FALSE,
  qr_code           VARCHAR(128),
  INDEX ix_connector_cp (charging_point_id),
  INDEX ix_connector_type_occupied (type, occupied),
  CONSTRAINT fk_connector_cp FOREIGN KEY (charging_point_id) REFERENCES charging_points(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== TARIFFS & SUBSCRIPTIONS ======
CREATE TABLE IF NOT EXISTS tariff_plans (
  id                    VARCHAR(36) PRIMARY KEY,
  name                  VARCHAR(120) NOT NULL,
  mode                  ENUM('PER_KWH','PER_MINUTE','HYBRID','SUBSCRIPTION') NOT NULL,
  price_per_kwh         DECIMAL(14,4) DEFAULT 0,
  price_per_minute      DECIMAL(14,4) DEFAULT 0,
  idle_fee_per_minute   DECIMAL(14,4) DEFAULT 0,
  time_of_use_rules     JSON NULL,
  active                BOOLEAN NOT NULL DEFAULT TRUE,
  INDEX ix_tariff_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS subscriptions (
  id         VARCHAR(36) PRIMARY KEY,
  driver_id  VARCHAR(36) NOT NULL,
  plan_id    VARCHAR(36) NOT NULL,
  start_date DATE NOT NULL,
  end_date   DATE,
  auto_renew BOOLEAN DEFAULT FALSE,
  status     ENUM('ACTIVE','PAUSED','CANCELLED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
  INDEX ix_sub_driver (driver_id),
  INDEX ix_sub_plan (plan_id),
  CONSTRAINT fk_sub_driver FOREIGN KEY (driver_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES tariff_plans(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== RESERVATIONS & CHARGING SESSIONS ======
CREATE TABLE IF NOT EXISTS reservations (
  id            VARCHAR(36) PRIMARY KEY,
  driver_id     VARCHAR(36) NOT NULL,
  connector_id  VARCHAR(36) NOT NULL,
  start_window  DATETIME(6) NOT NULL,
  end_window    DATETIME(6) NOT NULL,
  status        ENUM('HOLD','CONFIRMED','CANCELLED','EXPIRED') NOT NULL,
  INDEX ix_res_connector_window (connector_id, start_window, end_window),
  INDEX ix_res_driver (driver_id),
  CONSTRAINT fk_res_driver FOREIGN KEY (driver_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_res_connector FOREIGN KEY (connector_id) REFERENCES connectors(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS charging_sessions (
  id             VARCHAR(36) PRIMARY KEY,
  driver_id      VARCHAR(36) NOT NULL,
  vehicle_id     VARCHAR(36) NOT NULL,
  connector_id   VARCHAR(36) NOT NULL,
  reservation_id VARCHAR(36) NULL,
  status         ENUM('CREATED','RUNNING','STOPPED','BILLED','CANCELLED') NOT NULL,
  start_time     DATETIME(6),
  end_time       DATETIME(6),
  kwh_delivered  DOUBLE DEFAULT 0,
  energy_cost    DECIMAL(14,4) DEFAULT 0,
  time_cost      DECIMAL(14,4) DEFAULT 0,
  idle_fee       DECIMAL(14,4) DEFAULT 0,
  total_cost     DECIMAL(14,4) DEFAULT 0,
  INDEX ix_sess_driver_time (driver_id, start_time),
  INDEX ix_sess_connector (connector_id),
  CONSTRAINT fk_sess_driver FOREIGN KEY (driver_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_sess_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_sess_connector FOREIGN KEY (connector_id) REFERENCES connectors(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_sess_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== BILLING (INVOICES & PAYMENTS) ======
CREATE TABLE IF NOT EXISTS invoices (
  id          VARCHAR(36) PRIMARY KEY,
  driver_id   VARCHAR(36) NOT NULL,
  session_id  VARCHAR(36) NOT NULL,
  amount      DECIMAL(14,4) NOT NULL,
  tax_amount  DECIMAL(14,4) NOT NULL DEFAULT 0,
  currency    VARCHAR(8) NOT NULL,
  issued_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  status      ENUM('PENDING','PAID','VOID','REFUNDED') NOT NULL DEFAULT 'PENDING',
  pdf_url     VARCHAR(255),
  UNIQUE (session_id),
  INDEX ix_inv_driver_issued (driver_id, issued_at),
  CONSTRAINT fk_inv_driver FOREIGN KEY (driver_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_inv_session FOREIGN KEY (session_id) REFERENCES charging_sessions(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payments (
  id           VARCHAR(36) PRIMARY KEY,
  invoice_id   VARCHAR(36) NOT NULL,
  method       ENUM('EWALLET','BANKING','INTERNAL_WALLET') NOT NULL,
  status       ENUM('INIT','AUTHORIZED','SETTLED','FAILED','REFUNDED') NOT NULL DEFAULT 'INIT',
  amount       DECIMAL(14,4) NOT NULL,
  provider_ref VARCHAR(128),
  created_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  INDEX ix_pay_invoice (invoice_id),
  CONSTRAINT fk_pay_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== NOTIFICATIONS & INCIDENTS ======
CREATE TABLE IF NOT EXISTS notifications (
  id       VARCHAR(36) PRIMARY KEY,
  user_id  VARCHAR(36) NOT NULL,
  type     VARCHAR(32) NOT NULL,   -- e.g. SESSION, BILLING, SYSTEM
  content  TEXT NOT NULL,
  channel  ENUM('EMAIL','SMS','PUSH') NOT NULL,
  sent_at  DATETIME(6),
  INDEX ix_notif_user_sent (user_id, sent_at),
  CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS incident_tickets (
  id                  VARCHAR(36) PRIMARY KEY,
  station_id          VARCHAR(36) NOT NULL,
  connector_id        VARCHAR(36) NULL,
  reported_by_user_id VARCHAR(36) NOT NULL,
  title               VARCHAR(120) NOT NULL,
  description         TEXT,
  status              ENUM('OPEN','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
  created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  resolved_at         DATETIME(6) NULL,
  INDEX ix_incident_station (station_id),
  INDEX ix_incident_connector (connector_id),
  INDEX ix_incident_reporter (reported_by_user_id),
  CONSTRAINT fk_incident_station FOREIGN KEY (station_id) REFERENCES stations(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_incident_connector FOREIGN KEY (connector_id) REFERENCES connectors(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_incident_reporter FOREIGN KEY (reported_by_user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== Helpful indexes for search/analytics ======
CREATE INDEX IF NOT EXISTS ix_connector_occ ON connectors (occupied);
CREATE INDEX IF NOT EXISTS ix_res_status ON reservations (status);
CREATE INDEX IF NOT EXISTS ix_sess_status ON charging_sessions (status);
CREATE INDEX IF NOT EXISTS ix_pay_status ON payments (status);
CREATE INDEX IF NOT EXISTS ix_inv_status ON invoices (status);
