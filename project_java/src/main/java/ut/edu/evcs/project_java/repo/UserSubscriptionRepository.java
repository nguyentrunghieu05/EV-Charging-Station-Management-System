package ut.edu.evcs.project_java.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ut.edu.evcs.project_java.domain.subscription.UserSubscription;
import ut.edu.evcs.project_java.domain.subscription.SubscriptionStatus;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, String> {
    List<UserSubscription> findByUserId(String userId);
    List<UserSubscription> findByStatus(SubscriptionStatus status);
    
    @Query("SELECT us FROM UserSubscription us WHERE us.userId = :userId AND us.status = 'ACTIVE' ORDER BY us.endDate DESC")
    Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") String userId);
    
    @Query("SELECT COUNT(us) FROM UserSubscription us WHERE us.status = 'ACTIVE'")
    long countActiveSubscriptions();
}
