SET NAMES utf8mb4;

CREATE TEMPORARY TABLE stations_missing AS
SELECT id FROM stations s
WHERE s.status = 'ONLINE'
  AND NOT EXISTS (
    SELECT 1 FROM charging_points p WHERE p.station_id = s.id
  );

INSERT INTO charging_points (id, code, max_power_kw, online, station_id)
SELECT UUID(), CONCAT('CP-', LEFT(id, 8), '-01'), 150.0, TRUE, id
FROM stations_missing;

INSERT INTO charging_points (id, code, max_power_kw, online, station_id)
SELECT UUID(), CONCAT('CP-', LEFT(id, 8), '-02'), 50.0, TRUE, id
FROM stations_missing;

INSERT INTO connectors (id, type, max_current_a, voltage_v, occupied, qr_code, point_id)
SELECT UUID(), 'CCS', 200.0, 400.0, FALSE, CONCAT('QR-', cp.code, '-CCS'), cp.id
FROM charging_points cp
WHERE cp.station_id IN (SELECT id FROM stations_missing)
  AND cp.code LIKE 'CP-%-01';

INSERT INTO connectors (id, type, max_current_a, voltage_v, occupied, qr_code, point_id)
SELECT UUID(), 'CHAdeMO', 125.0, 400.0, FALSE, CONCAT('QR-', cp.code, '-CHAdeMO'), cp.id
FROM charging_points cp
WHERE cp.station_id IN (SELECT id FROM stations_missing)
  AND cp.code LIKE 'CP-%-01';

INSERT INTO connectors (id, type, max_current_a, voltage_v, occupied, qr_code, point_id)
SELECT UUID(), 'Type2', 32.0, 230.0, FALSE, CONCAT('QR-', cp.code, '-Type2'), cp.id
FROM charging_points cp
WHERE cp.station_id IN (SELECT id FROM stations_missing)
  AND cp.code LIKE 'CP-%-02';

DROP TEMPORARY TABLE stations_missing;