package ut.edu.evcs.project_java.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.session.*;

public interface ReservationRepository extends JpaRepository<Reservation, String> { // SỬA: Long -> String

}
