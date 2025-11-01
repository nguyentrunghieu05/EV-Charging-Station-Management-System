package ut.edu.evcs.project_java.service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.repo.InvoiceRepository;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;

@Service
public class PdfService {

    @Value("${app.files.invoice-dir:uploads/invoices}")
    private String invoiceDir;

    private final InvoiceRepository invoiceRepo;
    private final ChargingSessionRepository sessionRepo;

    public PdfService(InvoiceRepository invoiceRepo, ChargingSessionRepository sessionRepo) {
        this.invoiceRepo = invoiceRepo;
        this.sessionRepo = sessionRepo;
    }

    public String generateInvoicePdf(String invoiceId) {
        Invoice inv = invoiceRepo.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found"));
        ChargingSession s = inv.getSessionId() != null ? sessionRepo.findById(inv.getSessionId()).orElse(null) : null;

        String html = buildHtml(inv, s);

        try {
            Path dir = Path.of(invoiceDir);
            Files.createDirectories(dir);
            Path file = dir.resolve(invoiceId + ".pdf");
            try (OutputStream os = Files.newOutputStream(file)) {
                com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder = new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
                builder.withHtmlContent(html, null);
                builder.toStream(os);
                builder.run();
            }
            // return endpoint path (controller exposes /api/invoices/{id}/pdf)
            return "/api/invoices/" + invoiceId + "/pdf";
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private String buildHtml(Invoice inv, ChargingSession s) {
        String kwh = s != null ? String.format("%.3f", s.getKwhDelivered()) : "0.000";
        String energyCost = s != null ? s.getEnergyCost().toString() : "0.00";
        String timeCost = s != null ? s.getTimeCost().toString() : "0.00";
        String idleFee = s != null ? s.getIdleFee().toString() : "0.00";

        String html = "<html><head><meta charset='utf-8'><style>"
                + "body{font-family:sans-serif;font-size:12px}table{width:100%;border-collapse:collapse}"
                + "th,td{border:1px solid #ddd;padding:8px}" + ".tot{font-weight:bold}" + "</style></head><body>"
                + "<h2>HÓA ĐƠN THANH TOÁN</h2>"
                + "<p><b>Mã hóa đơn:</b> " + inv.getInvoiceNo() + "<br/>"
                + "<b>Ngày:</b> " + LocalDateTime.now() + "<br/>"
                + "<b>Khách hàng:</b> " + inv.getDriverId() + "</p>"
                + "<table><tr><th>Mục</th><th>Giá trị</th></tr>"
                + "<tr><td>Năng lượng (kWh)</td><td>" + kwh + "</td></tr>"
                + "<tr><td>Chi phí năng lượng (VND)</td><td>" + energyCost + "</td></tr>"
                + "<tr><td>Chi phí thời gian (VND)</td><td>" + timeCost + "</td></tr>"
                + "<tr><td>Idle fee (VND)</td><td>" + idleFee + "</td></tr>"
                + "<tr class='tot'><td>VAT (8%)</td><td>" + inv.getTaxAmount() + "</td></tr>"
                + "<tr class='tot'><td>Tổng thanh toán</td><td>" + inv.getTotalAmount() + "</td></tr>"
                + "</table><p>Cảm ơn.</p></body></html>";
        return html;
    }
}
