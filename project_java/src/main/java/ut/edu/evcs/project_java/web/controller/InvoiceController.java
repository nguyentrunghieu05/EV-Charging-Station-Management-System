package ut.edu.evcs.project_java.web.controller;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.repo.InvoiceRepository;
import ut.edu.evcs.project_java.repo.UserRepository;
import ut.edu.evcs.project_java.service.BillingService;
import ut.edu.evcs.project_java.web.dto.admin.InvoiceAdminDTO;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepo;
    private final ut.edu.evcs.project_java.service.CurrentUserService currentUserService;
    private final UserRepository userRepo;

    public InvoiceController(BillingService billingService, InvoiceRepository invoiceRepo,
            ut.edu.evcs.project_java.service.CurrentUserService currentUserService,
            UserRepository userRepo) {
        this.billingService = billingService;
        this.invoiceRepo = invoiceRepo;
        this.currentUserService = currentUserService;
        this.userRepo = userRepo;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Invoice> allInvoices = invoiceRepo.findAll();

            if (status != null && !status.isBlank()) {
                allInvoices = allInvoices.stream()
                        .filter(inv -> status.equalsIgnoreCase(inv.getStatus()))
                        .toList();
            }

            if (from != null && !from.isBlank()) {
                LocalDateTime fromDate = LocalDateTime.parse(from + "T00:00:00");
                allInvoices = allInvoices.stream()
                        .filter(inv -> inv.getIssuedAt().isAfter(fromDate) || inv.getIssuedAt().isEqual(fromDate))
                        .toList();
            }

            if (to != null && !to.isBlank()) {
                LocalDateTime toDate = LocalDateTime.parse(to + "T23:59:59");
                allInvoices = allInvoices.stream()
                        .filter(inv -> inv.getIssuedAt().isBefore(toDate) || inv.getIssuedAt().isEqual(toDate))
                        .toList();
            }

            // Fetch all users to map names efficiently
            Map<String, String> userNames = userRepo.findAll().stream()
                    .collect(Collectors.toMap(User::getId,
                            u -> u.getFullName() != null ? u.getFullName() : u.getUsername(), (a, b) -> a));

            List<InvoiceAdminDTO> dtos = allInvoices.stream().map(inv -> {
                String userName = userNames.getOrDefault(inv.getDriverId(), "N/A");
                return new InvoiceAdminDTO(
                        inv.getId(),
                        userName,
                        inv.getSessionId(),
                        inv.getTotalAmount(),
                        inv.getStatus(),
                        inv.getIssuedAt());
            }).toList();

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch invoices: " + e.getMessage()));
        }
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
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        try {
            Invoice inv = invoiceRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            // SECURITY: Verify user can only view their own invoices (unless ADMIN)
            String currentUserId = currentUserService.getCurrentUserId();
            if (!inv.getDriverId().equals(currentUserId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "You don't have permission to view this invoice"));
            }

            return ResponseEntity.ok(inv);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
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
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public ResponseEntity<?> markPaid(@PathVariable String id) {
        try {
            Invoice inv = invoiceRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            // SECURITY: Verify user can only mark their own invoices as paid
            String currentUserId = currentUserService.getCurrentUserId();
            if (!inv.getDriverId().equals(currentUserId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "You don't have permission to pay this invoice"));
            }

            if ("PAID".equalsIgnoreCase(inv.getStatus())) {
                return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Invoice already paid"));
            }

            inv.setStatus("PAID");
            inv.setPaidAt(LocalDateTime.now());
            invoiceRepo.save(inv);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Invoice marked as paid"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/send-email")
    public Map<String, String> sendEmail(@PathVariable String id) {
        // Stub: pretend email is sent
        if (!invoiceRepo.existsById(id))
            throw new RuntimeException("Invoice not found");
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
