package ut.edu.evcs.project_java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.repo.InvoiceRepository;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepo;
    private final ChargingSessionRepository sessionRepo;
    private final PdfService pdfService;

    public BillingService(InvoiceRepository invoiceRepo, ChargingSessionRepository sessionRepo, PdfService pdfService) {
        this.invoiceRepo = invoiceRepo;
        this.sessionRepo = sessionRepo;
        this.pdfService = pdfService;
    }

    @Transactional
    public Invoice createInvoice(String sessionId) {
        ChargingSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!"STOPPED".equalsIgnoreCase(s.getStatus()) && !"COMPLETED".equalsIgnoreCase(s.getStatus())) {
            throw new IllegalStateException(
                    "Session must be stopped before creating invoice. Current status: " + s.getStatus());
        }

        if (s.getEndTime() == null) {
            throw new IllegalStateException("Session has no endTime set");
        }

        Invoice existing = invoiceRepo.findFirstBySessionIdOrderByIssuedAtDesc(sessionId).orElse(null);
        if (existing != null) {
            return existing;
        }

        BigDecimal sessionTotal = s.getTotalCost();
        boolean useSessionCosts = (sessionTotal != null && sessionTotal.compareTo(BigDecimal.ZERO) > 0);

        BigDecimal energy;
        BigDecimal time;
        BigDecimal idle;
        BigDecimal subtotal;
        BigDecimal vat;
        BigDecimal total;

        if (useSessionCosts) {

            energy = s.getEnergyCost() != null ? s.getEnergyCost() : BigDecimal.ZERO;
            time = s.getTimeCost() != null ? s.getTimeCost() : BigDecimal.ZERO;
            idle = s.getIdleFee() != null ? s.getIdleFee() : BigDecimal.ZERO;
            total = sessionTotal.setScale(2, RoundingMode.HALF_UP);

            subtotal = total.divide(BigDecimal.valueOf(1.10), 2, RoundingMode.HALF_UP);
            vat = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
        } else {
            energy = s.getEnergyCost() == null ? BigDecimal.ZERO : s.getEnergyCost();
            if ((energy == null || energy.compareTo(BigDecimal.ZERO) == 0) && s.getKwhDelivered() > 0) {
                BigDecimal unit = BigDecimal.valueOf(3000);
                energy = BigDecimal.valueOf(s.getKwhDelivered()).multiply(unit).setScale(2, RoundingMode.HALF_UP);
            }
            time = s.getTimeCost() == null ? BigDecimal.ZERO : s.getTimeCost();
            idle = s.getIdleFee() == null ? BigDecimal.ZERO : s.getIdleFee();
            subtotal = energy.add(time).add(idle).setScale(2, RoundingMode.HALF_UP);
            vat = subtotal.multiply(BigDecimal.valueOf(10).movePointLeft(2)).setScale(2, RoundingMode.HALF_UP);
            total = subtotal.add(vat).setScale(2, RoundingMode.HALF_UP);
        }

        Invoice inv = new Invoice();
        inv.setDriverId(s.getDriverId());
        inv.setSessionId(s.getId());
        inv.setEnergyCost(energy);
        inv.setTimeCost(time);
        inv.setIdleFee(idle);
        inv.setServiceFee(time.add(idle));
        inv.setSubtotal(subtotal);
        inv.setTaxAmount(vat);
        inv.setTotalAmount(total);
        inv.setCurrency("VND");
        inv.setStatus("ISSUED");
        inv.setInvoiceNo(generateInvoiceNo());

        inv.setKwhDelivered(s.getKwhDelivered());
        inv.setUnitPrice(s.getUnitPriceVnd() != null ? s.getUnitPriceVnd() : new BigDecimal("3000"));

        inv = invoiceRepo.save(inv);

        try {
            String pdfUrl = pdfService.generateInvoicePdf(inv.getId());
            inv.setPdfUrl(pdfUrl);
        } catch (RuntimeException e) {
            inv.setPdfUrl(null);
        }
        return invoiceRepo.save(inv);
    }

    public List<Invoice> ensureInvoicesForDriver(String driverId) {
        List<ChargingSession> sessions = sessionRepo.findByDriverIdOrderByStartTimeDesc(driverId);
        List<Invoice> result = new ArrayList<>();
        for (ChargingSession s : sessions) {
            boolean ended = s.getEndTime() != null || "STOPPED".equalsIgnoreCase(s.getStatus())
                    || "COMPLETED".equalsIgnoreCase(s.getStatus());
            if (!ended)
                continue;
            Invoice existing = invoiceRepo.findFirstBySessionIdOrderByIssuedAtDesc(s.getId()).orElse(null);
            if (existing != null) {
                result.add(existing);
                continue;
            }
            try {
                Invoice created = createInvoice(s.getId());
                result.add(created);
            } catch (RuntimeException e) {

            }
        }
        return result;
    }

    private String generateInvoiceNo() {
        LocalDate now = LocalDate.now();
        int random = (int) (Math.random() * 90000) + 10000;
        return String.format("INV-%04d%02d-%05d", now.getYear(), now.getMonthValue(), random);
    }
}
