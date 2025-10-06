package ntcu.selab.SpringServer.service;

import lombok.RequiredArgsConstructor;
import ntcu.selab.SpringServer.domain.course.Course;
import ntcu.selab.SpringServer.repository.CourseRepository;
import ntcu.selab.SpringServer.repository.EnrollmentRepository;
import ntcu.selab.SpringServer.web.dto.course.CourseDto;
import ntcu.selab.SpringServer.web.mapper.CourseMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    /**
     * 取得所有學期（去重、排序）。
     */
    @Transactional(readOnly = true)
    public List<String> listSemesters() {
        return courseRepository.findAllSemesters();
    }

    /**
     * 依角色與選擇的學期回傳課程清單：
     * - teacher/ROOT：可見全部課程
     * - 其他（student/TA）：僅可見本人修課
     *
     * @param role   目前使用者角色（不區分大小寫）
     * @param userId 目前使用者 ID（學生或 TA 過濾用）
     * @param sem    查特定學期；為 null/空白則不以學期過濾
     */
    @Transactional(readOnly = true)
    public List<CourseDto> listCourses(String role, String userId, String sem) {
        boolean privileged = isPrivileged(role);

        List<Course> data;
        if (privileged) {
            // 老師/管理者：全部可見
            data = (sem == null || sem.isBlank())
                    ? courseRepository.findAll(Sort.by(Sort.Direction.ASC, "classId"))
                    : courseRepository.findBySemesterOrderByClassIdAsc(sem);
        } else {
            // 一般使用者：僅本人修課
            data = (sem == null || sem.isBlank())
                    ? enrollmentRepository.findCoursesByUserId(userId)
                    : enrollmentRepository.findCoursesByUserIdAndSemester(userId, sem);
        }
        return data.stream().map(courseMapper::toDto).toList();
    }

    private boolean isPrivileged(String role) {
        if (role == null) return false;
        String r = role.trim().toUpperCase();
        return "TEACHER".equals(r) || "ROOT".equals(r);
    }
}
