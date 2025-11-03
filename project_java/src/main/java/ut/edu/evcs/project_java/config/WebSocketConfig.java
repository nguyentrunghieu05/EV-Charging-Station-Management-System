package ut.edu.evcs.project_java.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Client sẽ subscribe tới /topic/... để nhận dữ liệu
        config.enableSimpleBroker("/topic");
        // Prefix cho các request gửi từ client
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Địa chỉ WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép truy cập từ frontend
                .withSockJS(); // Dự phòng khi client không hỗ trợ websocket
    }
}
