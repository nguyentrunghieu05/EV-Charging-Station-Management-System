package ut.edu.evcs.project_java.security;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key key;
    private final long accessTtlMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-ttl-minutes:15}") long accessTtlMinutes
    ) {
        // secret ≥ 256-bit cho HS256. Nếu bạn đang để base64, có thể dùng Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTtlMinutes = accessTtlMinutes;
    }

    public String issueAccess(String userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtlMinutes)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
