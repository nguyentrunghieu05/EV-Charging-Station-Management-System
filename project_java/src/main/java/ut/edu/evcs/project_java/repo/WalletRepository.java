package ut.edu.evcs.project_java.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.billing.*;

public interface WalletRepository extends JpaRepository<Wallet, String> {

}
