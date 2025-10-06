package ntcu.selab.SpringServer.service;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.domain.user.User;
import ntcu.selab.SpringServer.repository.UserRepository;
import ntcu.selab.SpringServer.security.JwtUtils;
import ntcu.selab.SpringServer.web.dto.auth.LoginRequest;
import ntcu.selab.SpringServer.web.dto.auth.LoginResponse;
import ntcu.selab.SpringServer.web.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder; // 由 SecurityBeans 提供
    private final JwtUtils jwt;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        User u = users.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("帳號或密碼錯誤"));

        boolean ok =
                u.getPassword() != null && encoder.matches(req.password(), u.getPassword());

        if (!ok) throw new IllegalArgumentException("帳號或密碼錯誤");

        String token = jwt.generate(u.getId(), u.getRole(),
                java.util.Map.of("name", u.getName()));
        return new LoginResponse(token, userMapper.toDto(u));
    }
}
