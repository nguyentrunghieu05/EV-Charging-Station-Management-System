-- V12__seed_tsn_connectors.sql
-- Seed charging points và connectors cho trạm "EVCS Sân Bay TSN"

SET NAMES utf8mb4;

-- Lấy station id theo tên
SET @tsn := (SELECT id FROM stations WHERE name = 'EVCS Sân Bay TSN' LIMIT 1);

-- Thêm charging points cho TSN
INSERT INTO charging_points (id, code, max_power_kw, online, station_id) VALUES
  (UUID(), 'CP-TSN-01', 150.0, TRUE, @tsn),
  (UUID(), 'CP-TSN-02', 50.0,  TRUE, @tsn);

-- Lấy charging point IDs
SET @cp_tsn_1 := (SELECT id FROM charging_points WHERE code = 'CP-TSN-01' LIMIT 1);
SET @cp_tsn_2 := (SELECT id FROM charging_points WHERE code = 'CP-TSN-02' LIMIT 1);

-- Thêm connectors cho TSN
INSERT INTO connectors (id, type, max_current_a, voltage_v, occupied, qr_code, point_id) VALUES
  -- CP-TSN-01 (DC Fast)
  (UUID(), 'CCS',     200.0, 400.0, FALSE, 'QR-TSN-01-CCS',     @cp_tsn_1),
  (UUID(), 'CHAdeMO', 125.0, 400.0, FALSE, 'QR-TSN-01-CHAdeMO', @cp_tsn_1),
  -- CP-TSN-02 (AC Type2)
  (UUID(), 'Type2',    32.0, 230.0, FALSE, 'QR-TSN-02-Type2',    @cp_tsn_2);