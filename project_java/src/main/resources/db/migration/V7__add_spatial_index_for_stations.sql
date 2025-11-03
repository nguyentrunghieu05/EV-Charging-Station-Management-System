-- V7__add_spatial_index_for_stations.sql
-- Thêm spatial index cho lat/lng để tối ưu tìm kiếm vị trí

-- Kiểm tra xem stations có POINT column chưa, nếu chưa thì thêm
SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'stations'
    AND column_name = 'location'
);

-- Nếu chưa có column location (POINT), thêm vào (vẫn là NULL ở bước này)
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE stations ADD COLUMN location POINT NULL AFTER lng',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Update location từ lat/lng hiện tại
UPDATE stations 
SET location = POINT(lng, lat)
WHERE location IS NULL AND lat IS NOT NULL AND lng IS NOT NULL;

-- ****** BẮT ĐẦU SỬA LỖI ******
-- BƯỚC 1: Cập nhật tất cả các 'location' CÒN LẠI (đang NULL) thành một giá trị mặc định (ví dụ: 0,0)
-- Điều này là BẮT BUỘC trước khi đặt cột thành NOT NULL.
UPDATE stations
SET location = POINT(0, 0)
WHERE location IS NULL;

-- BƯỚC 2: Sửa đổi cột location thành NOT NULL.
-- Chúng ta kiểm tra xem cột có đang cho phép NULL hay không trước khi sửa đổi.
SET @is_nullable := (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'stations'
    AND column_name = 'location'
    AND IS_NULLABLE = 'YES'
);

SET @sql := IF(@is_nullable = 1,
  'ALTER TABLE stations MODIFY COLUMN location POINT NOT NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
-- ****** KẾT THÚC SỬA LỖI ******


-- Thêm spatial index nếu chưa có
SET @idx_exists := (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'stations'
    AND index_name = 'idx_stations_location'
);

SET @sql := IF(@idx_exists = 0,
  -- Lệnh này bây giờ sẽ thành công vì cột 'location' đã là NOT NULL
  'ALTER TABLE stations ADD SPATIAL INDEX idx_stations_location (location)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Thêm regular index cho lat/lng (fallback nếu không dùng spatial)
SET @idx_lat := (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'stations'
    AND index_name = 'idx_stations_lat_lng'
);

SET @sql := IF(@idx_lat = 0,
  'CREATE INDEX idx_stations_lat_lng ON stations(lat, lng)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Thêm index cho status để filter ONLINE stations
SET @idx_status := (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'stations'
    AND index_name = 'idx_stations_status'
);

SET @sql := IF(@idx_status = 0,
  'CREATE INDEX idx_stations_status ON stations(status)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;