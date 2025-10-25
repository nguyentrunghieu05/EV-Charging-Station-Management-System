-- Users
CREATE TABLE IF NOT EXISTS users (
id VARCHAR(36) PRIMARY KEY,
email VARCHAR(255) NOT NULL UNIQUE,
phone VARCHAR(20),
full_name VARCHAR(255) NOT NULL,
password_hash VARCHAR(255),
type VARCHAR(20) NOT NULL
);
-- Stations
CREATE TABLE IF NOT EXISTS stations (
id VARCHAR(36) PRIMARY KEY,
name VARCHAR(255),
address VARCHAR(255),
lat DOUBLE,
lng DOUBLE,
status VARCHAR(20)
);
-- Charging points
CREATE TABLE IF NOT EXISTS charging_points (
id VARCHAR(36) PRIMARY KEY,
code VARCHAR(50),
max_power_kw DOUBLE,
online BOOLEAN,
station_id VARCHAR(36),
CONSTRAINT fk_point_station FOREIGN KEY (station_id) REFERENCES stations(id)
);
-- Connectors
CREATE TABLE IF NOT EXISTS connectors (
id VARCHAR(36) PRIMARY KEY,
type VARCHAR(20),
max_current_a DOUBLE,
voltage_v DOUBLE,
occupied BOOLEAN,
qr_code VARCHAR(100),
point_id VARCHAR(36),
CONSTRAINT fk_connector_point FOREIGN KEY (point_id) REFERENCES
charging_points(id)
)
