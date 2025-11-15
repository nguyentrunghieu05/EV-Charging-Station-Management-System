package ut.edu.evcs.project_java.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.billing.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    Optional<Invoice> findByInvoiceNo(String invoiceNo);
    List<Invoice> findByDriverIdOrderByIssuedAtDesc(String driverId);
    Optional<Invoice> findFirstBySessionIdOrderByIssuedAtDesc(String sessionId);
}
