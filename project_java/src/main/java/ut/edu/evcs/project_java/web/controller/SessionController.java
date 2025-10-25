package ut.edu.evcs.project_java.web.controller;

import org.springframework.web.bind.annotation.*;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.service.SessionService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService service;
    public SessionController(SessionService service) {
        this.service = service;
    }

@PostMapping("/start")
    public ChargingSession start(@RequestBody ChargingSession payload) {
        payload.setStatus("STARTED");
        return service.start(payload);
    }
}
