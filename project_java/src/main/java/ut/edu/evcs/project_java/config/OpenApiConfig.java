package ut.edu.evcs.project_java.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// thêm openapi definition và security scheme cho toàn bộ API
@OpenAPIDefinition(
    info = @Info(
        title = "EVCS API",
        version = "v1",
        description = "API for EV Charging Station Management System",
        contact = @io.swagger.v3.oas.annotations.info.Contact(
            name = "EVCS Dev Team",
            email = "support@evcs.local"
        )
    ),
    security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI evcsOpenAPI() {
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("EVCS API")
                .version("v1")
                .description("API for EV Charging Station Management System")
                .contact(new Contact()
                    .name("EVCS Dev Team")
                    .email("support@evcs.local")));
    }
}
