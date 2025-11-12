package ut.edu.evcs.project_java.web.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ut.edu.evcs.project_java.domain.incident.IncidentTicket;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.service.IncidentTicketService;
import ut.edu.evcs.project_java.service.SessionService;
import ut.edu.evcs.project_java.web.dto.staff.AssignIncidentRequest;
import ut.edu.evcs.project_java.web.dto.staff.IncidentTicketDTO;
import ut.edu.evcs.project_java.web.dto.staff.ManualStartSessionRequest;
import ut.edu.evcs.project_java.web.dto.staff.ManualStopSessionRequest;
import ut.edu.evcs.project_java.web.dto.staff.PriceOverrideRequest;
import ut.edu.evcs.project_java.web.dto.staff.ReportIncidentRequest;
import ut.edu.evcs.project_java.web.dto.staff.UpdateIncidentStatusRequest;

@Tag(name = "Staff Portal", description = "Staff operations for managing stations")
@RestController
@RequestMapping("/api/staff")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('CS_STAFF', 'ADMIN')")
public class StaffController {

    private final SessionService sessionService;
    private final IncidentTicketService incidentTicketService;

    public StaffController(SessionService sessionService,
                           IncidentTicketService incidentTicketService) {
        this.sessionService = sessionService;
        this.incidentTicketService = incidentTicketService;
    }

    // ===== SESSIONS =====

    @Operation(summary = "Manual start charging session")
    @PostMapping("/sessions/start")
    public ChargingSession manualStartSession(@Valid @RequestBody ManualStartSessionRequest request) {
        String staffUsername = getCurrentUsername();
        return sessionService.manualStartSession(
                request.getDriverId(),
                request.getConnectorId(),
                request.getVehicleId(),
                request.getNotes(),
                staffUsername
        );
    }

    @Operation(summary = "Manual stop charging session")
    @PostMapping("/sessions/{id}/stop")
    public ChargingSession manualStopSession(
            @PathVariable String id,
            @Valid @RequestBody ManualStopSessionRequest request) {
        return sessionService.stopSession(id, request.getFinalKwh());
    }

    @Operation(summary = "Override session pricing (ADMIN only)")
    @PutMapping("/sessions/{id}/override-price")
    @PreAuthorize("hasRole('ADMIN')")
    public ChargingSession overridePricing(
            @PathVariable String id,
            @Valid @RequestBody PriceOverrideRequest request) {

        String adminUsername = getCurrentUsername();
        return sessionService.overridePrice(
                id,
                request.getNewPricePerKwh(),
                request.getReason(),
                adminUsername
        );
    }

    @Operation(summary = "Get active sessions (optionally filter by station)")
    @GetMapping("/sessions/active")
    public Map<String, Object> getActiveSessions(
            @RequestParam(required = false) String stationId) {
        List<ChargingSession> sessions;

        if (stationId != null && !stationId.isBlank()) {
            sessions = sessionService.getActiveSessionsByStation(stationId);
        } else {
            sessions = sessionService.getActiveSessions();
        }
        return Map.of(
                "count", sessions.size(),
                "items", sessions
        );
    }

    // ===== INCIDENT APIs =====

    @Operation(summary = "Report incident at station")
    @PostMapping("/incidents")
    public IncidentTicketDTO reportIncident(@Valid @RequestBody ReportIncidentRequest request) {
        IncidentTicket ticket = new IncidentTicket();
        ticket.setStationId(request.getStationId());
        ticket.setConnectorId(request.getConnectorId());
        ticket.setSeverity(request.getSeverity());
        ticket.setDescription(request.getDescription());
        ticket.setAttachmentUrl(request.getAttachmentUrl());

        String currentUsername = getCurrentUsername();
        ticket.setReportedBy(currentUsername);

        IncidentTicket created = incidentTicketService.create(ticket);
        return toDto(created);
    }

    @Operation(summary = "Get incident detail")
    @GetMapping("/incidents/{id}")
    public IncidentTicketDTO getIncidentById(@PathVariable String id) {
        IncidentTicket ticket = incidentTicketService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
        return toDto(ticket);
    }

    @Operation(summary = "List incidents (filter by station / status / severity)")
    @GetMapping("/incidents")
    public List<IncidentTicketDTO> listIncidents(
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity) {

        List<IncidentTicket> tickets;

        if (stationId != null && !stationId.isBlank()) {
            tickets = incidentTicketService.getByStation(stationId);
        } else if (status != null && !status.isBlank()) {
            tickets = incidentTicketService.getByStatus(status);
        } else if (severity != null && !severity.isBlank()) {
            tickets = incidentTicketService.getBySeverity(severity);
        } else {
            tickets = incidentTicketService.getAll();
        }

        return tickets.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Update incident status")
    @PutMapping("/incidents/{id}/status")
    public IncidentTicketDTO updateIncidentStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateIncidentStatusRequest request) {

        IncidentTicket updated = incidentTicketService.updateStatus(
                id,
                request.getStatus(),
                request.getResolution()
        );
        return toDto(updated);
    }

    @Operation(summary = "Assign incident to staff")
    @PutMapping("/incidents/{id}/assign")
    public IncidentTicketDTO assignIncident(
            @PathVariable String id,
            @Valid @RequestBody AssignIncidentRequest request) {

        IncidentTicket updated = incidentTicketService.assignTo(id, request.getStaffId());
        return toDto(updated);
    }

    // ===== Helpers =====

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return auth.getName();
    }

    private IncidentTicketDTO toDto(IncidentTicket ticket) {
        IncidentTicketDTO dto = new IncidentTicketDTO();
        dto.setId(ticket.getId());
        dto.setStationId(ticket.getStationId());
        dto.setConnectorId(ticket.getConnectorId());
        dto.setSeverity(ticket.getSeverity());
        dto.setStatus(ticket.getStatus());
        dto.setDescription(ticket.getDescription());
        dto.setAttachmentUrl(ticket.getAttachmentUrl());
        dto.setReportedBy(ticket.getReportedBy());
        dto.setAssignedTo(ticket.getAssignedTo());
        dto.setCategory(ticket.getCategory());
        dto.setSource(ticket.getSource());
        dto.setResolution(ticket.getResolution());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        return dto;
    }
}
