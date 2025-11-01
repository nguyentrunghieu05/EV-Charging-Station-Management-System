INSERT INTO tariffs (id, name, mode, price_per_kwh, price_per_minute, idle_fee_per_minute, time_of_use_rules, active)
VALUES (UUID(), 'Default KWh', 'PER_KWH', 3000.00, 0.00, 0.00, NULL, TRUE);
