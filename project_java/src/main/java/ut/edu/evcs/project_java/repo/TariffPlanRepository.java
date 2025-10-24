package ut.edu.evcs.project_java.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.tariff.*;

public interface TariffPlanRepository extends JpaRepository<TariffPlan, String> {

}
