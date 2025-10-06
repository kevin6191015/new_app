package ntcu.selab.SpringServer.security;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("帳號不存在"));
        //回傳 Spring 的輕量 UserDetails；不要回傳 JPA 實體，避免 toString/序列化牽出雙向關聯
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())   // DB 必須是 BCrypt 雜湊（$2a$...）
                .roles(u.getRole())          // 例如 STUDENT / TEACHER / ROOT（不用自己加 ROLE_ 前綴）
                .build();
    }
}
