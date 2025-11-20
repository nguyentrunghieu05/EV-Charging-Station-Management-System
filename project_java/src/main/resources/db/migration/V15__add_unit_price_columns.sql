ALTER TABLE sessions ADD COLUMN unit_price_vnd DECIMAL(19,2);
ALTER TABLE invoices ADD COLUMN unit_price DECIMAL(19,2);
ALTER TABLE invoices ADD COLUMN kwh_delivered DOUBLE;
