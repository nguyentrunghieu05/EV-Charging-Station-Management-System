package ut.edu.evcs.project_java.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ut.edu.evcs.project_java.domain.incident.IncidentTicket;

public interface IncidentTicketRepository extends JpaRepository<IncidentTicket, String> {

    List<IncidentTicket> findByStationId(String stationId);

    List<IncidentTicket> findByStatus(String status);

    List<IncidentTicket> findByStatusIn(Collection<String> statuses);

    List<IncidentTicket> findBySeverityIgnoreCase(String severity);
}
