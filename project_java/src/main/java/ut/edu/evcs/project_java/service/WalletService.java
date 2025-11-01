package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ut.edu.evcs.project_java.domain.billing.Wallet;
import ut.edu.evcs.project_java.repo.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletService {
    private final WalletRepository repo;

    public WalletService(WalletRepository repo) {
        this.repo = repo;
    }

    /**
     * Lấy wallet của user, tự động tạo nếu chưa có
     */
    @Transactional
    public Wallet getOrCreateWallet(String userId) {
        Optional<Wallet> existing = repo.findAll().stream()
                .filter(w -> w.getOwnerUserId().equals(userId))
                .findFirst();
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Tạo wallet mới
        Wallet w = new Wallet();
        w.setOwnerUserId(userId);
        w.setBalance(BigDecimal.ZERO);
        w.setCurrency("VND");
        return repo.save(w);
    }

    /**
     * Lấy số dư wallet
     */
    public BigDecimal getBalance(String userId) {
        return getOrCreateWallet(userId).getBalance();
    }

    /**
     * Nạp tiền vào wallet (top-up)
     */
    @Transactional
    public Wallet topUp(String userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        Wallet w = getOrCreateWallet(userId);
        w.setBalance(w.getBalance().add(amount));
        return repo.save(w);
    }

    /**
     * Trừ tiền từ wallet (deduct)
     */
    @Transactional
    public Wallet deduct(String userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        Wallet w = getOrCreateWallet(userId);
        BigDecimal newBalance = w.getBalance().subtract(amount);
        
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient balance. Current: " + w.getBalance() + ", Required: " + amount);
        }
        
        w.setBalance(newBalance);
        return repo.save(w);
    }

    /**
     * Chuyển tiền giữa 2 wallet
     */
    @Transactional
    public void transfer(String fromUserId, String toUserId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to the same wallet");
        }
        
        // Trừ từ sender
        deduct(fromUserId, amount);
        
        // Cộng cho receiver
        topUp(toUserId, amount);
    }

    /**
     * Kiểm tra có đủ số dư không
     */
    public boolean hasSufficientBalance(String userId, BigDecimal requiredAmount) {
        if (requiredAmount == null || requiredAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        BigDecimal balance = getBalance(userId);
        return balance.compareTo(requiredAmount) >= 0;
    }

    /**
     * Set balance trực tiếp (chỉ dùng cho admin/testing)
     */
    @Transactional
    public Wallet setBalance(String userId, BigDecimal newBalance) {
        if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        
        Wallet w = getOrCreateWallet(userId);
        w.setBalance(newBalance);
        return repo.save(w);
    }

    /**
     * Lấy wallet by ID
     */
    public Optional<Wallet> getById(String walletId) {
        return repo.findById(walletId);
    }

    /**
     * Lấy wallet by userId
     */
    public Optional<Wallet> getByUserId(String userId) {
        return repo.findAll().stream()
                .filter(w -> w.getOwnerUserId().equals(userId))
                .findFirst();
    }
}