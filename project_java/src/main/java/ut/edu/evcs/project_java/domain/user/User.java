package ut.edu.evcs.project_java.domain.user;

import jakarta.persistence.*;
import lombok.*;
import ut.edu.evcs.project_java.domain.user.enums.UserType;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(length = 20)
    private String phone;
    @Column(nullable = false)
    private String fullName;
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;
}
