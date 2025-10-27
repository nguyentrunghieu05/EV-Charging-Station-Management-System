Tạo database: mysql -u root -p

CREATE DATABASE IF NOT EXISTS evcs
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'evcs'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON evcs.* TO 'evcs'@'localhost';
FLUSH PRIVILEGES;

Chạy java: .\mvnw clean -U spring-boot:run
