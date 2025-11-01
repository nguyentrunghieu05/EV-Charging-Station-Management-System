-- V2__add_vehicles_tariffs_and_money_decimal.sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 1) VEHICLES
-- =========================================================
CREATE TABLE IF NOT EXISTS vehicles (
    id                   VARCHAR(36) PRIMARY KEY,
    driver_id            VARCHAR(36) NOT NULL,
    brand                VARCHAR(100) NOT NULL,
    model                VARCHAR(100) NOT NULL,
    plate_no             VARCHAR(20)  NOT NULL,
    battery_capacity_kwh DOUBLE       NOT NULL DEFAULT 0,
    connector_supported  VARCHAR(20),
    CONSTRAINT fk_vehicle_driver FOREIGN KEY (driver_id)
        REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- idx_vehicle_driver
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'vehicles'
    AND index_name   = 'idx_vehicle_driver'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_vehicle_driver ON vehicles(driver_id)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- uq_vehicle_plate
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'vehicles'
    AND index_name   = 'uq_vehicle_plate'
);
SET @sql := IF(@exists = 0,
  'CREATE UNIQUE INDEX uq_vehicle_plate ON vehicles(plate_no)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================================================
-- 2) TARIFFS (time_of_use_rules = JSON, MySQL 8)
-- =========================================================
CREATE TABLE IF NOT EXISTS tariffs (
    id                   VARCHAR(36)  PRIMARY KEY,
    name                 VARCHAR(120) NOT NULL,
    mode                 VARCHAR(20)  NOT NULL, -- PER_KWH / PER_MINUTE / HYBRID / SUBSCRIPTION
    price_per_kwh        DECIMAL(18,4) NOT NULL DEFAULT 0,
    price_per_minute     DECIMAL(18,4) NOT NULL DEFAULT 0,
    idle_fee_per_minute  DECIMAL(18,4) NOT NULL DEFAULT 0,
    time_of_use_rules    JSON NULL,
    active               BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- uq_tariffs_name
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'tariffs'
    AND index_name   = 'uq_tariffs_name'
);
SET @sql := IF(@exists = 0,
  'CREATE UNIQUE INDEX uq_tariffs_name ON tariffs(name)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_tariffs_active
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'tariffs'
    AND index_name   = 'idx_tariffs_active'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_tariffs_active ON tariffs(active)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- idx_tariffs_mode
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'tariffs'
    AND index_name   = 'idx_tariffs_mode'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_tariffs_mode ON tariffs(mode)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================================================
-- 3) MONEY → DECIMAL(18,2)
-- =========================================================
-- invoices
ALTER TABLE invoices
  MODIFY total_amount DECIMAL(18,2) NOT NULL,
  MODIFY tax_amount   DECIMAL(18,2) NOT NULL DEFAULT 0;

-- payments
ALTER TABLE payments
  MODIFY amount DECIMAL(18,2) NOT NULL;

-- wallets
ALTER TABLE wallets
  MODIFY balance DECIMAL(18,2) NOT NULL DEFAULT 0;

-- sessions (chi phí)
ALTER TABLE sessions
  MODIFY energy_cost DECIMAL(18,2) NOT NULL DEFAULT 0,
  MODIFY time_cost   DECIMAL(18,2) NOT NULL DEFAULT 0,
  MODIFY idle_fee    DECIMAL(18,2) NOT NULL DEFAULT 0,
  MODIFY total_cost  DECIMAL(18,2) NOT NULL DEFAULT 0;

-- =========================================================
-- 4) INDEXES bổ sung cho truy vấn (driver, status, time)
-- =========================================================
-- Sessions: driver + thời gian + status
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'sessions'
    AND index_name   = 'idx_sessions_driver_start'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_sessions_driver_start ON sessions(driver_id, start_time)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'sessions'
    AND index_name   = 'idx_sessions_status_start'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_sessions_status_start ON sessions(status, start_time)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Reservations: connector + thời gian
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'reservations'
    AND index_name   = 'idx_reservations_conn_start'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_reservations_conn_start ON reservations(connector_id, start_window)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Invoices: driver + status + issued_at
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'invoices'
    AND index_name   = 'idx_invoices_driver_status_issued'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_invoices_driver_status_issued ON invoices(driver_id, status, issued_at)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Payments: invoice + created_at
SET @exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'payments'
    AND index_name   = 'idx_payments_invoice_created'
);
SET @sql := IF(@exists = 0,
  'CREATE INDEX idx_payments_invoice_created ON payments(invoice_id, created_at)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================================================
-- 5) FK chính sách ON DELETE (RESTRICT and SET NULL)
--    Sử dụng guard qua information_schema để DROP/ADD an toàn
-- =========================================================

-- sessions: driver
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_driver'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE sessions DROP FOREIGN KEY fk_session_driver',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_driver'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE sessions ADD CONSTRAINT fk_session_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- sessions: connector
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_connector'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE sessions DROP FOREIGN KEY fk_session_connector',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_connector'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE sessions ADD CONSTRAINT fk_session_connector FOREIGN KEY (connector_id) REFERENCES connectors(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- sessions: reservation_id
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_reservation'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE sessions DROP FOREIGN KEY fk_session_reservation',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_reservation'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE sessions ADD CONSTRAINT fk_session_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE SET NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- sessions: vehicle_id (chỉ ADD nếu chưa có)
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'sessions'
    AND constraint_name = 'fk_session_vehicle'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE sessions ADD CONSTRAINT fk_session_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE SET NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- invoices: driver
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'invoices'
    AND constraint_name = 'fk_invoice_driver'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE invoices DROP FOREIGN KEY fk_invoice_driver',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'invoices'
    AND constraint_name = 'fk_invoice_driver'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE invoices ADD CONSTRAINT fk_invoice_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- invoices: session_id
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'invoices'
    AND constraint_name = 'fk_invoice_session'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE invoices DROP FOREIGN KEY fk_invoice_session',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'invoices'
    AND constraint_name = 'fk_invoice_session'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE invoices ADD CONSTRAINT fk_invoice_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE SET NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- payments: invoice
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'payments'
    AND constraint_name = 'fk_payment_invoice'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE payments DROP FOREIGN KEY fk_payment_invoice',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'payments'
    AND constraint_name = 'fk_payment_invoice'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE payments ADD CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- reservations: driver
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'reservations'
    AND constraint_name = 'fk_reservation_driver'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE reservations DROP FOREIGN KEY fk_reservation_driver',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'reservations'
    AND constraint_name = 'fk_reservation_driver'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE reservations ADD CONSTRAINT fk_reservation_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- reservations: connector
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'reservations'
    AND constraint_name = 'fk_reservation_connector'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE reservations DROP FOREIGN KEY fk_reservation_connector',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'reservations'
    AND constraint_name = 'fk_reservation_connector'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE reservations ADD CONSTRAINT fk_reservation_connector FOREIGN KEY (connector_id) REFERENCES connectors(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- charging_points: station
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'charging_points'
    AND constraint_name = 'fk_point_station'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE charging_points DROP FOREIGN KEY fk_point_station',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'charging_points'
    AND constraint_name = 'fk_point_station'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE charging_points ADD CONSTRAINT fk_point_station FOREIGN KEY (station_id) REFERENCES stations(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- connectors: point
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'connectors'
    AND constraint_name = 'fk_connector_point'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE connectors DROP FOREIGN KEY fk_connector_point',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'connectors'
    AND constraint_name = 'fk_connector_point'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE connectors ADD CONSTRAINT fk_connector_point FOREIGN KEY (point_id) REFERENCES charging_points(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- wallets: owner
SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'wallets'
    AND constraint_name = 'fk_wallet_user'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 1,
  'ALTER TABLE wallets DROP FOREIGN KEY fk_wallet_user',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @fk := (
  SELECT COUNT(1) FROM information_schema.table_constraints
  WHERE constraint_schema = DATABASE()
    AND table_name = 'wallets'
    AND constraint_name = 'fk_wallet_user'
    AND constraint_type = 'FOREIGN KEY'
);
SET @sql := IF(@fk = 0,
  'ALTER TABLE wallets ADD CONSTRAINT fk_wallet_user FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE RESTRICT',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =========================================================
-- CHỐNG DOUBLE-BOOKING RESERVATIONS (MySQL 8 Trigger)
-- =========================================================
DROP TRIGGER IF EXISTS trg_reservation_before_ins;
DROP TRIGGER IF EXISTS trg_reservation_before_upd;

CREATE TRIGGER trg_reservation_before_ins
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
  -- start < end
  IF NEW.start_window >= NEW.end_window THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Invalid reservation window: start >= end';
  END IF;

  -- overlap check
  IF NEW.status <> 'CANCELLED' THEN
    IF EXISTS (
      SELECT 1
      FROM reservations r
      WHERE r.connector_id = NEW.connector_id
        AND r.status <> 'CANCELLED'
        AND NOT (NEW.end_window <= r.start_window OR NEW.start_window >= r.end_window)
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Reservation overlaps with existing booking';
    END IF;
  END IF;
END;

CREATE TRIGGER trg_reservation_before_upd
BEFORE UPDATE ON reservations
FOR EACH ROW
BEGIN
  -- start < end
  IF NEW.start_window >= NEW.end_window THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Invalid reservation window: start >= end';
  END IF;

  -- overlap check (bỏ qua chính nó)
  IF NEW.status <> 'CANCELLED' THEN
    IF EXISTS (
      SELECT 1
      FROM reservations r
      WHERE r.connector_id = NEW.connector_id
        AND r.id <> OLD.id
        AND r.status <> 'CANCELLED'
        AND NOT (NEW.end_window <= r.start_window OR NEW.start_window >= r.end_window)
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Reservation overlaps with existing booking';
    END IF;
  END IF;
END;

SET FOREIGN_KEY_CHECKS = 1;
