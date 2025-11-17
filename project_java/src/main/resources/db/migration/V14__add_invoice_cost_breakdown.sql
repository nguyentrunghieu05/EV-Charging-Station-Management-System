-- V14__add_invoice_cost_breakdown.sql
-- Add detailed cost breakdown columns to invoices table
ALTER TABLE invoices ADD COLUMN energy_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE invoices ADD COLUMN time_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE invoices ADD COLUMN idle_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE invoices ADD COLUMN service_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE invoices ADD COLUMN subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00;

-- Update existing invoices to have correct breakdown (all costs in total_amount for now)
UPDATE invoices SET 
    subtotal = total_amount - tax_amount,
    service_fee = total_amount - tax_amount,
    energy_cost = 0.00,
    time_cost = total_amount - tax_amount,
    idle_fee = 0.00
WHERE energy_cost IS NULL OR energy_cost = 0;
