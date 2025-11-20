package ut.edu.evcs.project_java.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.MultiValueMap;
import ut.edu.evcs.project_java.service.IVnPayService;
import ut.edu.evcs.project_java.repo.*;
import ut.edu.evcs.project_java.domain.billing.Invoice;
import ut.edu.evcs.project_java.domain.billing.Payment;
import ut.edu.evcs.project_java.web.dto.payment.PaymentInformationModel;
import ut.edu.evcs.project_java.web.dto.payment.PaymentResponseModel;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    private final IVnPayService vnPayService;
    private final InvoiceRepository invoiceRepo;
    private final ChargingSessionRepository sessionRepo;
    private final PaymentRepository paymentRepo;

    public VNPayController(IVnPayService vnPayService, InvoiceRepository invoiceRepo, ChargingSessionRepository sessionRepo, PaymentRepository paymentRepo) {
        this.vnPayService = vnPayService;
        this.invoiceRepo = invoiceRepo;
        this.sessionRepo = sessionRepo;
        this.paymentRepo = paymentRepo;
    }

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(HttpServletRequest request, @RequestBody PaymentInformationModel model) {
        try {
            System.out.println("--- CREATE PAYMENT REQUEST ---");
            System.out.println("Amount: " + model.getAmount());
            System.out.println("BankCode: " + model.getBankCode());

            String paymentUrl = vnPayService.createPaymentUrl(model, request);

            if (paymentUrl == null || paymentUrl.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("message", "Error: Cannot create payment URL"));
            }

            return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Successfully created payment URL",
                "url", paymentUrl,
                "data", paymentUrl
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Server Error: " + e.getMessage()));
        }
    }

    @GetMapping("/return")
    public ResponseEntity<?> handleReturn(@RequestParam MultiValueMap<String, String> params) {
        try {
            PaymentResponseModel response = vnPayService.paymentExecute(params);
            
            String invoiceId = null;
            String sessionId = null; // Biến để lưu session ID
            String orderInfo = params.getFirst("vnp_OrderInfo");
            
            if (orderInfo != null) {
                try {
                    // Logic bóc tách ID an toàn hơn
                    // Xử lý trường hợp "Thanh toan hoa don #<UUID>" hoặc "Order <UUID>"
                    String cleanInfo = orderInfo.replace("#", "").trim();
                    String[] parts = cleanInfo.split(" ");
                    if (parts.length > 0) {
                         invoiceId = parts[parts.length - 1].trim();
                    }
                    
                    // Cập nhật Database và Lấy SessionID
                    if (invoiceId != null && !invoiceId.isEmpty()) {
                         Invoice inv = invoiceRepo.findById(invoiceId).orElse(null);
                         
                         if (inv != null) {
                            // Lưu lại sessionId để redirect
                            sessionId = inv.getSessionId();

                            if (response.isSuccess() && !"PAID".equals(inv.getStatus())) {
                                inv.setStatus("PAID");
                                inv.setPaidAt(LocalDateTime.now());
                                invoiceRepo.save(inv);
                                
                                Payment p = new Payment(inv.getId(), "VNPay", "SETTLED", inv.getTotalAmount(), response.getTransactionId());
                                paymentRepo.save(p);
                                
                                if (sessionId != null) {
                                    sessionRepo.findById(sessionId).ifPresent(s -> {
                                        s.setStatus("COMPLETED");
                                        sessionRepo.save(s);
                                    });
                                }
                            }
                         }
                    }
                } catch (Exception ignore) {}
            }

            // --- QUAN TRỌNG: SỬA LẠI URL REDIRECT ---
            // Kèm theo sessionId để trang payment.html load được dữ liệu
            String redirectUrl = "http://localhost:8080/payment.html?status=" + (response.isSuccess() ? "success" : "fail");
            
            if (invoiceId != null) {
                 redirectUrl += "&invoiceId=" + invoiceId;
            }
            if (sessionId != null) {
                 redirectUrl += "&sessionId=" + sessionId; // <--- KHẮC PHỤC LỖI HÌNH 2
            }
            
            return ResponseEntity.status(302).header("Location", redirectUrl).build();

        } catch (Exception e) {
            e.printStackTrace();
             return ResponseEntity.status(302).header("Location", "http://localhost:8080/payment.html?status=error").build();
        }
    }

    @PostMapping("/ipn")
    public ResponseEntity<?> handleIPN(@RequestParam MultiValueMap<String, String> params) {
         return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
    }
}