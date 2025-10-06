package ntcu.selab.SpringServer.service;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.domain.user.User;
import ntcu.selab.SpringServer.repository.UserRepository;
import ntcu.selab.SpringServer.web.dto.user.UserDto;
import ntcu.selab.SpringServer.web.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder; // 由 SecurityBeans 提供

    @Transactional(readOnly = true)
    public UserDto getById(String id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return userMapper.toDto(u);
    }

    @Transactional(readOnly = true)
    public UserDto getByUsername(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return userMapper.toDto(u);
    }

    @Transactional(readOnly = true)
    public List<UserDto> listAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    /**
     * 建立新使用者（密碼必雜湊）。
     */
    @Transactional
    public UserDto create(String id, String username, String rawPassword, String name, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("username 已存在");
        }
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setPassword(encode(rawPassword));
        u.setName(name);
        u.setRole(role);
        return userMapper.toDto(userRepository.save(u));
    }

    /**
     * 更新名稱/角色（不含密碼）。
     */
    @Transactional
    public UserDto updateProfile(String id, String name, String role) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        u.setName(name);
        u.setRole(role);
        return userMapper.toDto(userRepository.save(u));
    }

    /**
     * 更新密碼（必雜湊；即使傳入看似已是雜湊，也重新雜湊統一管理）。
     */
    @Transactional
    public void changePassword(String id, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("密碼不可為空");
        }
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        u.setPassword(encode(rawPassword));
        userRepository.save(u);
    }

    @Transactional
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    // ---- 私有：一律雜湊 ----
    private String encode(String raw) {
        return encoder.encode(raw); // 全面統一使用 BCrypt
    }
}
