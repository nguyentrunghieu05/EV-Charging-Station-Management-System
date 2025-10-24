package ut.edu.evcs.project_java.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI evcsOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("EVCS API")
            .version("v1")
            .description("API for EV Charging Station Management System"));
    }
}