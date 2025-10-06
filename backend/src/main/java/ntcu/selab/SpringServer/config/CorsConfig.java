// src/main/java/ntcu/selab/SpringServer/config/CorsConfig.java
package ntcu.selab.SpringServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 允許的前端 Origin（依你的環境調整）
        cfg.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:5173"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        cfg.setExposedHeaders(List.of("Authorization"));   // 若前端要讀取 Authorization
        cfg.setAllowCredentials(true);                     // 若要帶 Cookie/憑證

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
