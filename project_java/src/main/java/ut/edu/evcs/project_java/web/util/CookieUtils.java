package ut.edu.evcs.project_java.web.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtils {
    private CookieUtils() {}

    public static void addRefreshCookie(HttpServletResponse resp, String token, int maxAgeSeconds) {
        Cookie c = new Cookie("refresh_token", token);
        c.setHttpOnly(true);
        // Dev đang dùng HTTP localhost → KHÔNG set Secure, khi lên HTTPS hãy bật:
        // c.setSecure(true);
        c.setPath("/api/auth"); // để /api/auth/refresh tự động nhận
        c.setMaxAge(maxAgeSeconds);
        resp.addCookie(c);
    }

    public static void clearRefreshCookie(HttpServletResponse resp) {
        Cookie c = new Cookie("refresh_token", "");
        c.setHttpOnly(true);
        // c.setSecure(true) khi chạy HTTPS
        c.setPath("/api/auth");
        c.setMaxAge(0);
        resp.addCookie(c);
    }
}
