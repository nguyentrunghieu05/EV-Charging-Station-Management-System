package ut.edu.evcs.project_java.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.billing.Wallet;
import ut.edu.evcs.project_java.service.WalletService;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "Wallets", description = "Quản lý ví tiền")
@RestController
@RequestMapping("/api/wallets")
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @Operation(summary = "Lấy ví của user (auto-create nếu chưa có)")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Wallet getOrCreateWallet(@PathVariable String userId) {
        return service.getOrCreateWallet(userId);
    }

    @Operation(summary = "Lấy số dư")
    @GetMapping("/balance/{userId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Map<String, Object> getBalance(@PathVariable String userId) {
        return Map.of(
            "userId", userId,
            "balance", service.getBalance(userId),
            "currency", "VND"
        );
    }

    @Operation(summary = "Nạp tiền vào ví")
    @PostMapping("/topup")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Wallet topUp(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        return service.topUp(userId, amount);
    }

    @Operation(summary = "Trừ tiền từ ví")
    @PostMapping("/deduct")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Wallet deduct(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        return service.deduct(userId, amount);
    }

    @Operation(summary = "Chuyển tiền giữa 2 ví")
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Map<String, String> transfer(@RequestBody Map<String, Object> request) {
        String fromUserId = (String) request.get("fromUserId");
        String toUserId = (String) request.get("toUserId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        
        service.transfer(fromUserId, toUserId, amount);
        
        return Map.of(
            "status", "success",
            "message", "Transfer completed"
        );
    }

    @Operation(summary = "Kiểm tra đủ tiền không")
    @GetMapping("/check-balance/{userId}")
    @PreAuthorize("hasAnyRole('EV_DRIVER', 'ADMIN')")
    public Map<String, Object> checkBalance(
            @PathVariable String userId,
            @RequestParam BigDecimal requiredAmount) {
        boolean sufficient = service.hasSufficientBalance(userId, requiredAmount);
        return Map.of(
            "userId", userId,
            "requiredAmount", requiredAmount,
            "currentBalance", service.getBalance(userId),
            "sufficient", sufficient
        );
    }

    @Operation(summary = "Set balance (Admin only)")
    @PutMapping("/set-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public Wallet setBalance(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        BigDecimal newBalance = new BigDecimal(request.get("balance").toString());
        return service.setBalance(userId, newBalance);
    }
}