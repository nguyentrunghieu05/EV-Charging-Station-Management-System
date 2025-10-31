package ut.edu.evcs.project_java.service;

import org.springframework.stereotype.Service;
import ut.edu.evcs.project_java.domain.user.RefreshToken;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.repo.RefreshTokenRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public RefreshToken issue(User user, String token, OffsetDateTime expiresAt, String ip, String ua) {
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(token);
        rt.setIssuedAt(OffsetDateTime.now());
        rt.setExpiresAt(expiresAt);
        rt.setRevoked(false);
        rt.setIp(ip);
        rt.setUserAgent(ua);
        return repo.save(rt);
    }

    public Optional<RefreshToken> findValid(String token) {
        return repo.findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiresAt().isAfter(OffsetDateTime.now()));
    }

    public void revoke(RefreshToken rt) {
        rt.setRevoked(true);
        repo.save(rt);
    }
}
