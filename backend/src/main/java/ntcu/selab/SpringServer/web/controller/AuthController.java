package ntcu.selab.SpringServer.web.controller;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.security.JwtUtils;
import ntcu.selab.SpringServer.service.UserService;
import ntcu.selab.SpringServer.web.api.ApiEnvelope;
import ntcu.selab.SpringServer.web.dto.auth.LoginRequest;
import ntcu.selab.SpringServer.web.dto.auth.LoginResponse;
import ntcu.selab.SpringServer.web.dto.user.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiEnvelope<LoginResponse>> login(@RequestBody LoginRequest req) {
        // 透過 Spring Security 驗證帳號/密碼
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        // 找使用者資料
        UserDto user = userService.getByUsername(req.username());
        

        // 產生 JWT
        String token = jwtUtils.generate(
                user.id(), user.role(), Map.of("name", user.name())
        );

        return ResponseEntity.ok(ApiEnvelope.ok(new LoginResponse(token, user)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiEnvelope<UserDto>> me(@RequestHeader("Authorization") String authz) {
        String token = authz.replaceFirst("(?i)^Bearer\\s+", "");
        String uid = jwtUtils.getUserId(token);
        UserDto user = userService.getById(uid);
        return ResponseEntity.ok(ApiEnvelope.ok(user));
    }
}
