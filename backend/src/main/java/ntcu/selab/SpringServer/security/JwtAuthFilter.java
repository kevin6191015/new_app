package ntcu.selab.SpringServer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    /** 跳過不需驗證的路徑（登入、健康檢查、CORS 預檢等） */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String path   = request.getServletPath(); // 若有 context-path，這裡已扣掉

        if ("OPTIONS".equalsIgnoreCase(method)) return true;          // CORS 預檢
        if (path.startsWith("/auth/")) return true;                   // 登入等匿名端點
        if (path.startsWith("/actuator/")) return true;               // Actuator（視需求）
        if (path.equals("/health")) return true;                      // 自訂健康檢查（視需求）

        return false;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authz = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authz != null && authz.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String token = authz.substring(7).trim();
            try {
                // 解析並驗簽（若無效會丟 JwtException）
                String userId = jwtUtils.getUserId(token);
                String role   = jwtUtils.getRole(token);

                if (userId != null && role != null) {
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    // principal 採用 userId（字串）；credentials 設為 null
                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                // Token 無效/過期：清空 context，交由後續授權規則決定（通常 401）
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
