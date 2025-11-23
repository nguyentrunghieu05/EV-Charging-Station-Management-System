-- ======================================================================
-- V16__create_subscription_plans.sql
-- SUBSCRIPTION PLANS TABLE
-- Bảng quản lý các gói thuê bao (trả trước, trả sau, VIP membership)
-- ======================================================================

CREATE TABLE IF NOT EXISTS subscription_plans (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    plan_type       VARCHAR(50) NOT NULL,  -- PREPAID, POSTPAID, VIP_MONTHLY, VIP_YEARLY
    price           DECIMAL(10,2) NOT NULL DEFAULT 0,
    duration_days   INT NOT NULL DEFAULT 30,
    
    -- Benefits
    discount_percent DECIMAL(5,2) DEFAULT 0,  -- % chiết khấu cho mọi giao dịch
    free_kwh        DECIMAL(10,2) DEFAULT 0, -- Số kWh miễn phí mỗi tháng
    priority_access  BOOLEAN DEFAULT FALSE,   -- Ưu tiên đặt chỗ
    
    -- Status
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    description     TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ======================================================================
-- USER SUBSCRIPTIONS TABLE
-- Bảng lưu thông tin đăng ký gói của user
-- ======================================================================

CREATE TABLE IF NOT EXISTS user_subscriptions (
    id              VARCHAR(36) PRIMARY KEY,
    user_id         VARCHAR(36) NOT NULL,
    plan_id         VARCHAR(36) NOT NULL,
    
    start_date      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date        TIMESTAMP NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, EXPIRED, CANCELLED
    
    kwh_used        DECIMAL(10,2) DEFAULT 0,  -- Số kWh đã dùng trong chu kỳ hiện tại
    
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_subscription_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_plan
        FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_subscriptions_user ON user_subscriptions(user_id);
CREATE INDEX idx_user_subscriptions_plan ON user_subscriptions(plan_id);
CREATE INDEX idx_user_subscriptions_status ON user_subscriptions(status);

-- ======================================================================
-- SEED DATA: Default Subscription Plans
-- ======================================================================

INSERT INTO subscription_plans (id, name, plan_type, price, duration_days, discount_percent, free_kwh, priority_access, description, is_active) VALUES
('sp-01', 'Trả trước cơ bản', 'PREPAID', 100000, 30, 0, 0, FALSE, 'Gói trả trước cơ bản cho khách hàng cá nhân', TRUE),
('sp-02', 'Trả trước cao cấp', 'PREPAID', 500000, 30, 5, 20, FALSE, 'Gói trả trước cao cấp với chiết khấu 5% và 20 kWh miễn phí', TRUE),
('sp-03', 'Trả sau doanh nghiệp', 'POSTPAID', 0, 30, 10, 0, TRUE, 'Gói trả sau dành cho doanh nghiệp, thanh toán cuối tháng', TRUE),
('sp-04', 'VIP Tháng', 'VIP_MONTHLY', 1000000, 30, 15, 100, TRUE, 'Gói VIP tháng với ưu đãi đặc biệt: 15% giảm giá, 100 kWh miễn phí, ưu tiên đặt chỗ', TRUE),
('sp-05', 'VIP Năm', 'VIP_YEARLY', 10000000, 365, 20, 150, TRUE, 'Gói VIP năm với ưu đãi tốt nhất: 20% giảm giá, 150 kWh miễn phí/tháng, ưu tiên đặt chỗ', TRUE);
