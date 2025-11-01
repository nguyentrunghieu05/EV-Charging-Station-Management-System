package ut.edu.evcs.project_java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

        if (!"STOPPED".equalsIgnoreCase(s.getStatus())) {
            throw new RuntimeException("Session must be STOPPED to create invoice.");
        }

        BigDecimal subtotal = s.getEnergyCost().add(s.getTimeCost()).add(s.getIdleFee());
        BigDecimal vat = subtotal.multiply(BigDecimal.valueOf(8).movePointLeft(2)).setScale(2, RoundingMode.HALF_UP);
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

        String pdfUrl = pdfService.generateInvoicePdf(inv.getId());
        inv.setPdfUrl(pdfUrl);
        return invoiceRepo.save(inv);
    }

    private String generateInvoiceNo() {
        LocalDate now = LocalDate.now();
        int random = (int)(Math.random() * 90000) + 10000;
        return String.format("INV-%04d%02d-%05d", now.getYear(), now.getMonthValue(), random);
    }
}
