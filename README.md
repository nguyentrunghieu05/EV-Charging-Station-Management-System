Create database: mysql -u root -p

DROP DATABASE IF EXISTS evcs; (drop DB cũ nếu có)

CREATE DATABASE IF NOT EXISTS evcs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'evcs'@'%' IDENTIFIED BY 'evcs123';

GRANT ALL PRIVILEGES ON evcs.* TO 'evcs'@'%';

FLUSH PRIVILEGES;

Run migration + compile: .\mvnw clean compile flyway:migrate

build jar: .\mvnw package

Run web: java -jar target/project_java-0.0.1-SNAPSHOT.jar
