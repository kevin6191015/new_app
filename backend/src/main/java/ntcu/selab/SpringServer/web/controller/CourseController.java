package ntcu.selab.SpringServer.web.controller;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.security.JwtUtils;
import ntcu.selab.SpringServer.service.CourseService;
import ntcu.selab.SpringServer.web.api.ApiEnvelope;
import ntcu.selab.SpringServer.web.dto.course.CourseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final JwtUtils jwtUtils;

    // 前端: GET /courses/semesters
    @GetMapping("/semesters")
    public ResponseEntity<ApiEnvelope<List<String>>> listSemesters() {
        return ResponseEntity.ok(ApiEnvelope.ok(courseService.listSemesters()));
    }

    // 前端: GET /courses?sem=2025-Spring
    @GetMapping
    public ResponseEntity<ApiEnvelope<List<CourseDto>>> listCourses(
            @RequestHeader(value = "Authorization", required = false) String authz,
            @RequestParam(required = false) String sem
    ) {
        // 若此 API 需要權限，請將 required = true 並在 Security 設 authenticated()
        String role = "ANON";
        String userId = "ANON";
        if (authz != null && !authz.isBlank()) {
            String token = authz.replaceFirst("(?i)^Bearer\\s+", "");
            role = jwtUtils.getRole(token);
            userId = jwtUtils.getUserId(token);
        }

        return ResponseEntity.ok(ApiEnvelope.ok(
                courseService.listCourses(role, userId, sem)
        ));
    }
}
