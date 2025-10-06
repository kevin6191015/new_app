package ntcu.selab.SpringServer.web.controller;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.service.UserService;
import ntcu.selab.SpringServer.web.api.ApiEnvelope;
import ntcu.selab.SpringServer.web.dto.user.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 使用者 CRUD。
 * 若要細分 Request DTO 可額外建立，但這裡直接用簡單參數。
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiEnvelope<List<UserDto>>> listAll() {
        return ResponseEntity.ok(ApiEnvelope.ok(userService.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiEnvelope<UserDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiEnvelope.ok(userService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiEnvelope<UserDto>> create(@RequestParam String id,
                                          @RequestParam String username,
                                          @RequestParam String password,
                                          @RequestParam String name,
                                          @RequestParam String role) {
        UserDto created = userService.create(id, username, password, name, role);
        return ResponseEntity
                .created(URI.create("/users/" + created.id()))
                .body(ApiEnvelope.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiEnvelope<UserDto>> updateProfile(@PathVariable String id,
                                                 @RequestParam String name,
                                                 @RequestParam String role) {
        return ResponseEntity.ok(ApiEnvelope.ok(userService.updateProfile(id, name, role)));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiEnvelope<Void>> changePassword(@PathVariable String id,
                                               @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok(ApiEnvelope.ok(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiEnvelope<Void>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiEnvelope.ok(null));
    }
}
