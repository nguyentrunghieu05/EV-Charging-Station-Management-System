-- V10__seed_admin_and_staff_users.sql
-- Seed tài khoản DEV: admin01 (ADMIN), staff01 (CS_STAFF)

-- ADMIN  (password = admin123)
-- hash: $2b$10$qfw8INH4Es.UiZmHJt5LMOifrTUTaKo0V/2vvTC.wuA9hd4yPN1UW
INSERT INTO users (id, email, username, phone, full_name, password_hash, type)
VALUES (UUID(), 'admin01@example.com', 'admin01', '0123456789', 'System Admin','$2b$10$qfw8INH4Es.UiZmHJt5LMOifrTUTaKo0V/2vvTC.wuA9hd4yPN1UW','ADMIN')
ON DUPLICATE KEY UPDATE
  email         = VALUES(email),
  phone         = VALUES(phone),
  full_name     = VALUES(full_name),
  password_hash = VALUES(password_hash),
  type          = VALUES(type);

-- CS_STAFF  (password = staff123)
-- hash: $2b$10$iSoOzrvBIFFrH1azVpHn5OxmqJkcEIAbX/mMt17JbcoUlY2K6QSge
INSERT INTO users (id, email, username, phone, full_name, password_hash, type)
VALUES (UUID(), 'staff01@example.com', 'staff01', '0987654321', 'Charging Staff','$2b$10$iSoOzrvBIFFrH1azVpHn5OxmqJkcEIAbX/mMt17JbcoUlY2K6QSge','CS_STAFF')
ON DUPLICATE KEY UPDATE
  email         = VALUES(email),
  phone         = VALUES(phone),
  full_name     = VALUES(full_name),
  password_hash = VALUES(password_hash),
  type          = VALUES(type);
