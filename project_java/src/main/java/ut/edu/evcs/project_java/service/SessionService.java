package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.session.ChargingSession;
import ut.edu.evcs.project_java.repo.ChargingSessionRepository;

@Service
public class SessionService {
    private final ChargingSessionRepository repo;
    public SessionService(ChargingSessionRepository repo) {
        this.repo = repo;
    }
    public ChargingSession start(ChargingSession s) {
        return repo.save(s);
    }
}
