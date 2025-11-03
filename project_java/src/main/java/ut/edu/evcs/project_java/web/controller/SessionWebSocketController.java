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
        // Khi client gửi dữ liệu qua /app/session/{sessionId}
        // thì server sẽ broadcast lại thông tin tới /topic/session-updates
        System.out.println("Received update: " + update);
        return update;
    }
}
