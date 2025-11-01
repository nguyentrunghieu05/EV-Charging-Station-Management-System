package ut.edu.evcs.project_java.web.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.service.SessionService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/{id}/stop")
    public ChargingSession stopSession(@PathVariable("id") String id, @RequestBody(required=false) Map<String,Object> body) {
        BigDecimal finalKwh = null;
        if (body != null && body.get("finalKwh") != null) {
            finalKwh = new BigDecimal(body.get("finalKwh").toString());
        }
        return sessionService.stopSession(id, finalKwh);
    }
}
