package ut.edu.evcs.project_java.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ut.edu.evcs.project_java.domain.session.ChargingSession;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, String> {

    List<ChargingSession> findByStatusIn(List<String> of);
}
