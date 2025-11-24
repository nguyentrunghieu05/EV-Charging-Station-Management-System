package ut.edu.evcs.project_java.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.station.Station;
import ut.edu.evcs.project_java.domain.station.StationStatus;

public interface StationRepository extends JpaRepository<Station, String> {
    long countByStatusNot(StationStatus status);
}
