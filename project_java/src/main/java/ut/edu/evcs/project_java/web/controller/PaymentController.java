package ut.edu.evcs.project_java.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.repo.InvoiceRepository;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;
import ut.edu.evcs.project_java.service.WalletService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final InvoiceRepository invoiceRepo;
    private final WalletService walletService;
    private final ChargingSessionRepository sessionRepo;

    public PaymentController(InvoiceRepository invoiceRepo, WalletService walletService, ChargingSessionRepository sessionRepo) {
        this.invoiceRepo = invoiceRepo;
        this.walletService = walletService;
        this.sessionRepo = sessionRepo;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EV_DRIVER','ADMIN')")
    public ResponseEntity<?> pay(@RequestBody Map<String, Object> body) {
        try {
            String invoiceId = (String) body.get("invoiceId");
            String method = (String) body.get("paymentMethod");
            String userId = (String) body.get("userId");
            Object amtObj = body.get("amount");
            if (invoiceId == null || method == null || userId == null || amtObj == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thiếu tham số thanh toán"));
            }
            BigDecimal amount = new BigDecimal(amtObj.toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Số tiền phải > 0"));
            }

            Invoice inv = invoiceRepo.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            if ("PAID".equalsIgnoreCase(inv.getStatus())) {
                return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Invoice already paid"));
            }

            if ("wallet".equalsIgnoreCase(method)) {
                if (!walletService.hasSufficientBalance(userId, amount)) {
                    return ResponseEntity.status(400).body(Map.of("message", "Số dư ví không đủ"));
                }
                walletService.deduct(userId, amount);
            }
            // banking/card/qr: pretend success

            inv.setStatus("PAID");
            inv.setPaidAt(LocalDateTime.now());
            invoiceRepo.save(inv);

            if (inv.getSessionId() != null) {
                sessionRepo.findById(inv.getSessionId()).ifPresent(s -> {
                    s.setStatus("COMPLETED");
                    sessionRepo.save(s);
                });
            }

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Payment settled",
                    "invoiceId", inv.getId()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }
}