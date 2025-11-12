package ut.edu.evcs.project_java.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ut.edu.evcs.project_java.domain.incident.IncidentTicket;
import ut.edu.evcs.project_java.repo.IncidentTicketRepository;

@Service
public class IncidentTicketService {

    private final IncidentTicketRepository repo;

    public IncidentTicketService(IncidentTicketRepository repo) {
        this.repo = repo;
    }

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final String STATUS_CLOSED = "CLOSED";

    /**
     * Tạo incident ticket mới
     */
    @Transactional
    public IncidentTicket create(IncidentTicket ticket) {
        if (ticket.getStationId() == null || ticket.getStationId().isBlank()) {
            throw new IllegalArgumentException("stationId is required");
        }
        if (ticket.getSeverity() == null || ticket.getSeverity().isBlank()) {
            throw new IllegalArgumentException("severity is required");
        }
        if (ticket.getDescription() == null || ticket.getDescription().isBlank()) {
            throw new IllegalArgumentException("description is required");
        }

        // Default values
        if (ticket.getStatus() == null || ticket.getStatus().isBlank()) {
            ticket.setStatus(STATUS_OPEN);
        }
        if (ticket.getSource() == null || ticket.getSource().isBlank()) {
            ticket.setSource("STAFF_PORTAL");
        }

        LocalDateTime now = LocalDateTime.now();
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);

        return repo.save(ticket);
    }

    /**
     * Update ticket status
     */
    @Transactional
    public IncidentTicket updateStatus(String id, String newStatus, String resolution) {
        IncidentTicket ticket = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        if (STATUS_RESOLVED.equals(newStatus) || STATUS_CLOSED.equals(newStatus)) {
            ticket.setResolvedAt(LocalDateTime.now());
            if (resolution != null && !resolution.isBlank()) {
                ticket.setResolution(resolution);
            }
        }

        return repo.save(ticket);
    }

    /**
     * Assign ticket to staff
     */
    @Transactional
    public IncidentTicket assignTo(String id, String staffId) {
        IncidentTicket ticket = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));

        ticket.setAssignedTo(staffId);
        ticket.setStatus(STATUS_IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());

        return repo.save(ticket);
    }

    /**
     * Get tickets by station
     */
    public List<IncidentTicket> getByStation(String stationId) {
        return repo.findByStationId(stationId);
    }

    /**
     * Get open tickets (OPEN + IN_PROGRESS)
     */
    public List<IncidentTicket> getOpenTickets() {
        return repo.findByStatusIn(List.of(STATUS_OPEN, STATUS_IN_PROGRESS));
    }

    /**
     * Get tickets by severity
     */
    public List<IncidentTicket> getBySeverity(String severity) {
        return repo.findBySeverityIgnoreCase(severity);
    }

    /**
     * Get tickets by status
     */
    public List<IncidentTicket> getByStatus(String status) {
        return repo.findByStatus(status);
    }

    /**
     * Get ticket by ID
     */
    public Optional<IncidentTicket> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Get all tickets
     */
    public List<IncidentTicket> getAll() {
        return repo.findAll();
    }
}
