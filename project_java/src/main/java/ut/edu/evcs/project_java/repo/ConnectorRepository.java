package ut.edu.evcs.project_java.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.station.*;

public interface ConnectorRepository extends JpaRepository<Connector, String> {
    List<Connector> findByChargingPoint(ChargingPoint chargingPoint);
}
