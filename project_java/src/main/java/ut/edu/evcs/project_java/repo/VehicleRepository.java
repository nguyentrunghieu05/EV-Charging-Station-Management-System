package ut.edu.evcs.project_java.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.vehicle.*;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

}

