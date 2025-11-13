package ut.edu.evcs.project_java.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.repo.UserRepository;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in SecurityContext");
        }

        Object principal = auth.getPrincipal();
        String subject;

        if (principal instanceof UserDetails userDetails) {
            subject = userDetails.getUsername();
        } else if (principal instanceof String s) {
            subject = s;
        } else {
            throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
        }

        User user = userRepository.findByEmail(subject)
                .or(() -> userRepository.findByUsername(subject))
                .orElseThrow(() -> new IllegalStateException("User not found for subject: " + subject));

        return user.getId();
    }
}
