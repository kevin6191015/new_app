package ntcu.selab.SpringServer.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private final String secret;
    private final long expMinutes;

    public JwtUtils(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.exp-minutes:120}") long expMinutes
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Missing property: app.jwt.secret");
        }
        // HS256 建議 ≥ 64 bytes
        if (secret.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalStateException("app.jwt.secret too short (>=64 bytes recommended for HS256).");
        }
        this.secret = secret;
        this.expMinutes = expMinutes;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 發 JWT（sub=userId, claim: role + extraClaims） */
    public String generate(String userId, String role, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        JwtBuilder b = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expMinutes * 60)))
                .claim("role", role);
        if (extraClaims != null && !extraClaims.isEmpty()) {
            extraClaims.forEach(b::claim);
        }
        return b.signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public String generate(String userId, String role) {
        return generate(userId, role, Map.of());
    }

    /** 驗簽 + 解析 claims（驗證失敗會丟例外） */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token);
    }

    public String getUserId(String token) { return parse(token).getBody().getSubject(); }

    public String getRole(String token) {
        Object r = parse(token).getBody().get("role");
        return r != null ? r.toString() : null;
    }
}
