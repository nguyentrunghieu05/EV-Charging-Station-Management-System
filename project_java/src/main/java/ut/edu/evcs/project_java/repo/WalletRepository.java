package ut.edu.evcs.project_java.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.billing.*;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByOwnerUserId(String ownerUserId);
}
