package ut.edu.evcs.project_java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ut.edu.evcs.project_java.security.CustomUserDetailsService;
import ut.edu.evcs.project_java.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService uds;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomUserDetailsService uds) {
        this.jwtFilter = jwtFilter;
        this.uds = uds;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/auth.html",
                                "/home.html",

                                "/scan.html",
                                "/charging.html",
                                "/payment.html",
                                "/invoice.html",
                                "/invoices.html",
                                "/favicon.ico",
                                "/stations.html",
                                "/reservation.html",

                                "/vehicles.html",
                                "/sessions.html",
                                "/notifications.html",

                                "/assets/**",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",

                                "/admin/dashboard.html",
                                "/admin/users.html",
                                "/admin/stations.html",
                                "/admin/subscriptions.html",
                                "/admin/incidents.html",
                                "/admin/analytics.html",
                                "/admin/billing.html",
                                "/admin/**.css",
                                "/admin/**.js",

                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health",

                                "/api/auth/**",

                                "/api/vnpay/test",
                                "/api/vnpay/return",
                                "/api/vnpay/ipn")
                        .permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}