package ut.edu.evcs.project_java.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.tariff.TariffPlan;

public interface TariffPlanRepository extends JpaRepository<TariffPlan, String> {
    Optional<TariffPlan> findFirstByActiveTrue();
}
