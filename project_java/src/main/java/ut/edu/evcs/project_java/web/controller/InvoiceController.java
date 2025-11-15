package ut.edu.evcs.project_java.web.controller;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.repo.InvoiceRepository;
import ut.edu.evcs.project_java.service.BillingService;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepo;
    private final ut.edu.evcs.project_java.service.CurrentUserService currentUserService;

    public InvoiceController(BillingService billingService, InvoiceRepository invoiceRepo, ut.edu.evcs.project_java.service.CurrentUserService currentUserService) {
        this.billingService = billingService;
        this.invoiceRepo = invoiceRepo;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "sessionId is required"));
        }
        try {
            Invoice inv = billingService.createInvoice(sessionId);
            return ResponseEntity.ok(inv);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public Invoice getById(@PathVariable("id") String id) {
        return invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getBySession(@PathVariable String sessionId) {
        return invoiceRepo.findFirstBySessionIdOrderByIssuedAtDesc(sessionId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    try {
                        Invoice created = billingService.createInvoice(sessionId);
                        return ResponseEntity.ok(created);
                    } catch (RuntimeException e) {
                        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
                    }
                });
    }

    @GetMapping("/user/{userId}")
    public List<Invoice> getByUser(@PathVariable String userId) {
        return invoiceRepo.findByDriverIdOrderByIssuedAtDesc(userId);
    }

    @GetMapping("/me")
    public List<Invoice> getMyInvoices() {
        String uid = currentUserService.getCurrentUserId();
        billingService.ensureInvoicesForDriver(uid);
        return invoiceRepo.findByDriverIdOrderByIssuedAtDesc(uid);
    }

    @PostMapping("/{id}/pay")
    public Map<String, String> markPaid(@PathVariable String id) {
        Invoice inv = invoiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        inv.setStatus("PAID");
        inv.setPaidAt(LocalDateTime.now());
        invoiceRepo.save(inv);
        return Map.of("status", "SUCCESS", "message", "Invoice paid");
    }

    @PostMapping("/{id}/send-email")
    public Map<String, String> sendEmail(@PathVariable String id) {
        // Stub: pretend email is sent
        if (!invoiceRepo.existsById(id)) throw new RuntimeException("Invoice not found");
        return Map.of("status", "SENT", "message", "Email đã được gửi");
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<FileSystemResource> downloadPdf(@PathVariable("id") String id) {
        Path p = Path.of("uploads/invoices/" + id + ".pdf");
        FileSystemResource resource = new FileSystemResource(p);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=\"" + id + ".pdf\"")
                .body(resource);
    }
}
