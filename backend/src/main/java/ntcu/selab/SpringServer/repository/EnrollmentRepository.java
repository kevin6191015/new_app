package ntcu.selab.SpringServer.repository;

import ntcu.selab.SpringServer.domain.course.Course;
import ntcu.selab.SpringServer.domain.enrollment.Enrollment;
import ntcu.selab.SpringServer.domain.enrollment.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {

    @Query("""
           select e.course
           from Enrollment e
           where e.user.id = :userId
           order by e.course.classId
           """)
    List<Course> findCoursesByUserId(@Param("userId") String userId);

    @Query("""
           select e.course
           from Enrollment e
           where e.user.id = :userId and e.course.semester = :sem
           order by e.course.classId
           """)
    List<Course> findCoursesByUserIdAndSemester(@Param("userId") String userId,
                                                @Param("sem") String sem);
}