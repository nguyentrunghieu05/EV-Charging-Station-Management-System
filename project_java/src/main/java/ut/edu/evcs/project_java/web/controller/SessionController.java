package ut.edu.evcs.project_java.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.service.SessionService;
import ut.edu.evcs.project_java.web.dto.CreateSessionRequest;
import ut.edu.evcs.project_java.web.dto.SessionResponse;

@Tag(name = "Charging Sessions")
@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService service;
    public SessionController(SessionService service) { this.service = service; }

    @Operation(summary = "Bắt đầu phiên sạc")
    @PostMapping("/start")
    public SessionResponse start(@Valid @RequestBody CreateSessionRequest req) {
        // map request -> entity
        ChargingSession s = new ChargingSession();
        s.setDriverId(req.getDriverId());
        s.setVehicleId(req.getVehicleId());
        s.setConnectorId(req.getConnectorId());
        s.setStatus("STARTED");

        ChargingSession saved = service.start(s);

        // map entity -> response
        SessionResponse res = new SessionResponse();
        res.setId(saved.getId());
        res.setDriverId(saved.getDriverId());
        res.setVehicleId(saved.getVehicleId());
        res.setConnectorId(saved.getConnectorId());
        res.setReservationId(saved.getReservationId());
        res.setStatus(saved.getStatus());
        res.setStartTime(saved.getStartTime());
        res.setEndTime(saved.getEndTime());
        res.setKwhDelivered(saved.getKwhDelivered());
        res.setEnergyCost(saved.getEnergyCost());
        res.setTimeCost(saved.getTimeCost());
        res.setIdleFee(saved.getIdleFee());
        res.setTotalCost(saved.getTotalCost());
        return res;
    }
}
