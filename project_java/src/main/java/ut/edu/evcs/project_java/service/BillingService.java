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

        Invoice existing = invoiceRepo.findFirstBySessionIdOrderByIssuedAtDesc(sessionId).orElse(null);
        if (existing != null) {
            return existing;
        }

        BigDecimal energy = s.getEnergyCost() == null ? BigDecimal.ZERO : s.getEnergyCost();
        BigDecimal time = s.getTimeCost() == null ? BigDecimal.ZERO : s.getTimeCost();
        BigDecimal idle = s.getIdleFee() == null ? BigDecimal.ZERO : s.getIdleFee();
        BigDecimal subtotal = energy.add(time).add(idle).setScale(2, RoundingMode.HALF_UP);
        BigDecimal vat = subtotal.multiply(BigDecimal.valueOf(10).movePointLeft(2)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(vat).setScale(2, RoundingMode.HALF_UP);

        Invoice inv = new Invoice();
        inv.setDriverId(s.getDriverId());
        inv.setSessionId(s.getId());
        inv.setTotalAmount(total);
        inv.setTaxAmount(vat);
        inv.setCurrency("VND");
        inv.setStatus("ISSUED");
        inv.setInvoiceNo(generateInvoiceNo());
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
            boolean ended = s.getEndTime() != null || "STOPPED".equalsIgnoreCase(s.getStatus()) || "COMPLETED".equalsIgnoreCase(s.getStatus());
            if (!ended) continue;
            Invoice existing = invoiceRepo.findFirstBySessionIdOrderByIssuedAtDesc(s.getId()).orElse(null);
            if (existing != null) {
                result.add(existing);
                continue;
            }
            Invoice created = createInvoice(s.getId());
            result.add(created);
        }
        return result;
    }

    private String generateInvoiceNo() {
        LocalDate now = LocalDate.now();
        int random = (int)(Math.random() * 90000) + 10000;
        return String.format("INV-%04d%02d-%05d", now.getYear(), now.getMonthValue(), random);
    }
}
