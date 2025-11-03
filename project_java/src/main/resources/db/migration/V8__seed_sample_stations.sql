-- V8__seed_sample_stations.sql
-- Seed dữ liệu mẫu cho stations ở khu vực TP.HCM

SET NAMES utf8mb4;

-- Insert sample stations
INSERT INTO stations (id, name, address, lat, lng, status, location) VALUES
-- Quận 1
(UUID(), 'EVCS Nguyễn Huệ', 'Đường Nguyễn Huệ, Quận 1, TP.HCM', 10.7744, 106.7019, 'ONLINE', POINT(106.7019, 10.7744)),
(UUID(), 'EVCS Bến Thành Market', '123 Lê Lợi, Quận 1, TP.HCM', 10.7729, 106.6980, 'ONLINE', POINT(106.6980, 10.7729)),

-- Quận 3
(UUID(), 'EVCS Cộng Hòa', '456 Cộng Hòa, Quận 3, TP.HCM', 10.7857, 106.6659, 'ONLINE', POINT(106.6659, 10.7857)),
(UUID(), 'EVCS CMT8', '789 Cách Mạng Tháng 8, Quận 3, TP.HCM', 10.7823, 106.6743, 'ONLINE', POINT(106.6743, 10.7823)),

-- Quận 7
(UUID(), 'EVCS Phú Mỹ Hưng', 'Nguyễn Lương Bằng, Quận 7, TP.HCM', 10.7290, 106.7204, 'ONLINE', POINT(106.7204, 10.7290)),
(UUID(), 'EVCS Crescent Mall', 'Đại lộ Nguyễn Văn Linh, Quận 7, TP.HCM', 10.7282, 106.7197, 'ONLINE', POINT(106.7197, 10.7282)),

-- Quận 2 (Thủ Đức)
(UUID(), 'EVCS Thảo Điền', 'Quốc Hương, Quận 2, TP.HCM', 10.8016, 106.7310, 'ONLINE', POINT(106.7310, 10.8016)),
(UUID(), 'EVCS Xa lộ Hà Nội', '234 Xa lộ Hà Nội, Quận 2, TP.HCM', 10.8324, 106.7744, 'ONLINE', POINT(106.7744, 10.8324)),

-- Bình Thạnh
(UUID(), 'EVCS Điện Biên Phủ', '567 Điện Biên Phủ, Bình Thạnh, TP.HCM', 10.8023, 106.7110, 'ONLINE', POINT(106.7110, 10.8023)),
(UUID(), 'EVCS Vincom Bình Thạnh', 'Ung Văn Khiêm, Bình Thạnh, TP.HCM', 10.8087, 106.7129, 'ONLINE', POINT(106.7129, 10.8087)),

-- Tân Bình
(UUID(), 'EVCS Sân Bay TSN', 'Trường Sơn, Tân Bình, TP.HCM', 10.8166, 106.6593, 'ONLINE', POINT(106.6593, 10.8166)),
(UUID(), 'EVCS Hoàng Văn Thụ', '890 Hoàng Văn Thụ, Tân Bình, TP.HCM', 10.7997, 106.6523, 'ONLINE', POINT(106.6523, 10.7997)),

-- Thêm vài station OFFLINE/MAINTENANCE để test
(UUID(), 'EVCS Maintenance Test', 'Test Address, Quận 5', 10.7520, 106.6720, 'MAINTENANCE', POINT(106.6720, 10.7520)),
(UUID(), 'EVCS Offline Test', 'Test Address 2, Quận 6', 10.7380, 106.6300, 'OFFLINE', POINT(106.6300, 10.7380));

-- Lấy 3 station IDs để seed charging points và connectors
SET @station1 := (SELECT id FROM stations WHERE name = 'EVCS Nguyễn Huệ' LIMIT 1);
SET @station2 := (SELECT id FROM stations WHERE name = 'EVCS Phú Mỹ Hưng' LIMIT 1);
SET @station3 := (SELECT id FROM stations WHERE name = 'EVCS Thảo Điền' LIMIT 1);

-- Insert charging points
INSERT INTO charging_points (id, code, max_power_kw, online, station_id) VALUES
-- Station 1: 2 charging points
(UUID(), 'CP-NH-01', 150.0, TRUE, @station1),
(UUID(), 'CP-NH-02', 50.0, TRUE, @station1),

-- Station 2: 3 charging points
(UUID(), 'CP-PMH-01', 150.0, TRUE, @station2),
(UUID(), 'CP-PMH-02', 150.0, TRUE, @station2),
(UUID(), 'CP-PMH-03', 50.0, TRUE, @station2),

-- Station 3: 2 charging points
(UUID(), 'CP-TD-01', 150.0, TRUE, @station3),
(UUID(), 'CP-TD-02', 100.0, TRUE, @station3);

-- Lấy charging point IDs
SET @cp1 := (SELECT id FROM charging_points WHERE code = 'CP-NH-01' LIMIT 1);
SET @cp2 := (SELECT id FROM charging_points WHERE code = 'CP-NH-02' LIMIT 1);
SET @cp3 := (SELECT id FROM charging_points WHERE code = 'CP-PMH-01' LIMIT 1);
SET @cp4 := (SELECT id FROM charging_points WHERE code = 'CP-PMH-02' LIMIT 1);
SET @cp5 := (SELECT id FROM charging_points WHERE code = 'CP-TD-01' LIMIT 1);

-- Insert connectors
INSERT INTO connectors (id, type, max_current_a, voltage_v, occupied, qr_code, point_id) VALUES
-- Charging Point 1 (DC Fast)
(UUID(), 'CCS', 200.0, 400.0, FALSE, 'QR-NH-01-CCS', @cp1),
(UUID(), 'CHAdeMO', 125.0, 400.0, FALSE, 'QR-NH-01-CHAdeMO', @cp1),

-- Charging Point 2 (AC)
(UUID(), 'Type2', 32.0, 230.0, FALSE, 'QR-NH-02-Type2', @cp2),

-- Charging Point 3 (DC Fast)
(UUID(), 'CCS', 200.0, 400.0, FALSE, 'QR-PMH-01-CCS', @cp3),
(UUID(), 'CHAdeMO', 125.0, 400.0, FALSE, 'QR-PMH-01-CHAdeMO', @cp3),

-- Charging Point 4 (DC Fast)
(UUID(), 'CCS', 200.0, 400.0, FALSE, 'QR-PMH-02-CCS', @cp4),

-- Charging Point 5 (DC Fast)
(UUID(), 'CCS', 200.0, 400.0, FALSE, 'QR-TD-01-CCS', @cp5),
(UUID(), 'CHAdeMO', 125.0, 400.0, FALSE, 'QR-TD-01-CHAdeMO', @cp5);