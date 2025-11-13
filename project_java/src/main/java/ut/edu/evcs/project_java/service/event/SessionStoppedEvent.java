package ut.edu.evcs.project_java.service.event;

import ut.edu.evcs.project_java.domain.session.ChargingSession;

public class SessionStoppedEvent {

    private final ChargingSession session;

    public SessionStoppedEvent(ChargingSession session) {
        this.session = session;
    }

    public ChargingSession getSession() {
        return session;
    }
}
