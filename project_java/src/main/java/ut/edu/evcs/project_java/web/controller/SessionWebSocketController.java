package ut.edu.evcs.project_java.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import ut.edu.evcs.project_java.web.dto.SessionStatusUpdate;

@Controller
public class SessionWebSocketController {

    @MessageMapping("/session/{sessionId}")
    @SendTo("/topic/session-updates")
    public SessionStatusUpdate getSessionStatus(SessionStatusUpdate update) {
        System.out.println("Received update: " + update);
        return update;
    }
}
