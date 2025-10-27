package ut.edu.evcs.project_java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    UserDetailsService users() {
        return new InMemoryUserDetailsManager(
            User.withUsername("admin").password("{noop}admin123").roles("ADMIN", "USER").build()
            );
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/assets/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                .anyRequest().permitAll()
)
            .httpBasic(Customizer.withDefaults());
        return http.build();
}
}