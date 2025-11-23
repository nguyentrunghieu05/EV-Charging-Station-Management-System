package ut.edu.evcs.project_java.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.domain.user.enums.UserType;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByType(UserType type);
}
