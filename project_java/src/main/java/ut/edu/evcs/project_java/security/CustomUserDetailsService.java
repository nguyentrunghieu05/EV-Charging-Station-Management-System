package ut.edu.evcs.project_java.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.domain.user.enums.UserType;
import ut.edu.evcs.project_java.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        Optional<User> found = emailOrUsername.contains("@")
                ? users.findByEmail(emailOrUsername)
                : users.findByUsername(emailOrUsername);
        User u = found.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + mapRole(u.getType())));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash() == null ? "" : u.getPasswordHash())
                .authorities(authorities)
                .accountLocked(false).accountExpired(false).credentialsExpired(false).disabled(false)
                .build();
    }

    private String mapRole(UserType type) {
        return switch (type) {
            case ADMIN -> "ADMIN";
            case CS_STAFF -> "CS_STAFF";
            case EV_DRIVER -> "EV_DRIVER";
        };
    }
}
