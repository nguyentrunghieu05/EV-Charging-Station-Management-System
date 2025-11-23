package ut.edu.evcs.project_java.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ut.edu.evcs.project_java.domain.subscription.SubscriptionPlan;
import ut.edu.evcs.project_java.domain.subscription.PlanType;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, String> {
    List<SubscriptionPlan> findByIsActiveTrue();

    List<SubscriptionPlan> findByPlanType(PlanType planType);
}
