package ut.edu.evcs.project_java.web.controller;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ut.edu.evcs.project_java.domain.user.RefreshToken;
import ut.edu.evcs.project_java.domain.user.User;
import ut.edu.evcs.project_java.domain.user.enums.UserType;
import ut.edu.evcs.project_java.repo.UserRepository;
import ut.edu.evcs.project_java.security.JwtTokenService;
import ut.edu.evcs.project_java.service.RefreshTokenService;
import ut.edu.evcs.project_java.web.dto.auth.AuthResponse;
import ut.edu.evcs.project_java.web.dto.auth.LoginRequest;
import ut.edu.evcs.project_java.web.dto.auth.RegisterRequest;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;
    private final UserRepository users;
    private final JwtTokenService jwt;
    private final RefreshTokenService refreshSvc;

    public AuthController(AuthenticationManager authManager, PasswordEncoder encoder,
                          UserRepository users, JwtTokenService jwt, RefreshTokenService refreshSvc) {
        this.authManager = authManager;
        this.encoder = encoder;
        this.users = users;
        this.jwt = jwt;
        this.refreshSvc = refreshSvc;
    }

    @Value("${app.jwt.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;
    @Value("${app.jwt.refresh-cookie-domain:localhost}")
    private String refreshCookieDomain;
    @Value("${app.jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;
    @Value("${app.jwt.refresh-cookie-samesite:Lax}")
    private String refreshCookieSameSite;

    @Operation(summary = "Đăng ký")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req,
                                                 HttpServletRequest httpReq, HttpServletResponse httpRes) {
        if (users.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (users.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        User u = new User();
        u.setEmail(req.getEmail());
        u.setUsername(req.getUsername());
        u.setFullName(req.getFullName());
        u.setPhone(req.getPhone());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setType(UserType.EV_DRIVER); // role mặc định

        users.save(u);

        return issueTokensAndRespond(u, httpReq, httpRes);
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req,
                                              HttpServletRequest httpReq, HttpServletResponse httpRes) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmailOrUsername(), req.getPassword())
        );
        // principal.username = username
        String username = auth.getName();
        User u = users.findByUsername(username).orElseThrow();

        return issueTokensAndRespond(u, httpReq, httpRes);
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        // Đọc cookie theo tên cấu hình (tránh dùng @CookieValue với placeholder)
        Cookie c = WebUtils.getCookie(httpReq, refreshCookieName);
        String refreshTokenCookie = (c == null) ? null : c.getValue();

        if (refreshTokenCookie == null || refreshTokenCookie.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        RefreshToken rt = refreshSvc.findValid(refreshTokenCookie).orElse(null);
        if (rt == null) {
            return ResponseEntity.status(401).build();
        }

        // *** TỐI ƯU: không chạm rt.getUser() ***
        // Lấy thông tin user chủ động bằng repository (1 query, không lazy)
        User u = users.findById(rt.getUserId()).orElse(null);
        if (u == null) {
            // Token mồ côi → revoke phòng hờ
            refreshSvc.revoke(rt);
            return ResponseEntity.status(401).build();
        }

        String access = jwt.generateAccessToken(u.getUsername(), Map.of(
                "uid", u.getId(), "email", u.getEmail(), "role", u.getType().name()
        ));

        AuthResponse body = buildAuthResponse(u, access);
        // (tuỳ chọn) sliding refresh: nếu muốn rotate thì tạo refresh mới ở đây

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(refreshTokenCookie, rt.getExpiresAt()).toString())
                .body(body);
    }

    @Operation(summary = "Logout (revoke refresh)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpReq, HttpServletResponse httpRes) {
        Cookie c = WebUtils.getCookie(httpReq, refreshCookieName);
        if (c != null && c.getValue() != null && !c.getValue().isBlank()) {
            refreshSvc.findValid(c.getValue()).ifPresent(refreshSvc::revoke);
        }
        // Xoá cookie
        ResponseCookie clear = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true).secure(refreshCookieSecure).path("/")
                .maxAge(0)
                .domain(refreshCookieDomain)
                .sameSite(refreshCookieSameSite)
                .build();
        httpRes.addHeader(HttpHeaders.SET_COOKIE, clear.toString());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Thông tin user hiện tại")
    @GetMapping("/me")
    public Map<String, Object> me(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        if (principal == null) return Map.of("authenticated", false);
        User u = users.findByUsername(principal.getUsername()).orElseThrow();
        return Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "username", u.getUsername(),
                "fullName", u.getFullName(),
                "type", u.getType().name()
        );
    }

    // ===== helpers =====
    private ResponseEntity<AuthResponse> issueTokensAndRespond(User u, HttpServletRequest httpReq, HttpServletResponse httpRes) {
        String access = jwt.generateAccessToken(u.getUsername(), Map.of(
                "uid", u.getId(), "email", u.getEmail(), "role", u.getType().name()
        ));
        String refresh = jwt.generateRefreshToken(u.getUsername());

        // lưu refresh vào DB (đã dùng userId phẳng trong RefreshTokenService)
        var exp = OffsetDateTime.ofInstant(
                jwt.parse(refresh).getBody().getExpiration().toInstant(), ZoneOffset.UTC);
        String ip = clientIp(httpReq);
        String ua = httpReq.getHeader("User-Agent");
        RefreshToken saved = refreshSvc.issue(u, refresh, exp, ip, ua);

        AuthResponse body = buildAuthResponse(u, access);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(saved.getToken(), exp).toString())
                .body(body);
    }

    private AuthResponse buildAuthResponse(User u, String access) {
        AuthResponse res = new AuthResponse();
        res.setAccessToken(access);
        res.setExpiresIn(60L * 15); // seconds, align with access ttl
        var v = new AuthResponse.UserView();
        v.id = u.getId();
        v.email = u.getEmail();
        v.username = u.getUsername();
        v.fullName = u.getFullName();
        v.type = u.getType().name();
        res.setUser(v);
        return res;
    }

    private ResponseCookie buildRefreshCookie(String token, OffsetDateTime exp) {
        long maxAge = Math.max(0, exp.toEpochSecond() - OffsetDateTime.now().toEpochSecond());
        return ResponseCookie.from(refreshCookieName, token)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .domain(refreshCookieDomain)
                .path("/")
                .maxAge(maxAge)
                .sameSite(refreshCookieSameSite)
                .build();
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
