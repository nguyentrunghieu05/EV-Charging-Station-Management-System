ALTER TABLE sessions
  ADD COLUMN meter_start_kwh DECIMAL(18,3) NULL,
  ADD COLUMN meter_end_kwh   DECIMAL(18,3) NULL,
  ADD COLUMN tariff_id       VARCHAR(36) NULL;

ALTER TABLE invoices
  ADD COLUMN invoice_no VARCHAR(30) NULL;

CREATE UNIQUE INDEX uq_invoices_invoice_no ON invoices(invoice_no);
CREATE INDEX idx_sessions_tariff ON sessions(tariff_id);
