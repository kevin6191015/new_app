package ntcu.selab.SpringServer.repository;

import ntcu.selab.SpringServer.domain.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("select distinct c.semester from Course c order by c.semester")
    List<String> findAllSemesters();

    List<Course> findBySemesterOrderByClassIdAsc(String semester);
}
