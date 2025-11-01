package ut.edu.evcs.project_java.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.billing.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByProviderRef(String providerRef);
}
