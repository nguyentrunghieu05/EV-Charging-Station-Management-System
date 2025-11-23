package ut.edu.evcs.project_java.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ut.edu.evcs.project_java.domain.user.RefreshToken;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.repo.RefreshTokenRepository;
import ut.edu.evcs.project_java.security.JwtService;
import ut.edu.evcs.project_java.web.dto.TokenPair;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository repo, JwtService jwtService) {
        this.repo = repo;
        this.jwtService = jwtService;
    }

    public RefreshToken issue(User user, String token, OffsetDateTime expiresAt, String ip, String ua) {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
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

    public TokenPair refresh(String rawRefreshToken) {
        RefreshToken rt = findValid(rawRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));

        String userId = rt.getUserId();
        String access = jwtService.issueAccess(userId);
        return new TokenPair(access);
    }
}
