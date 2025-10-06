package ntcu.selab.SpringServer.web.mapper;

import ntcu.selab.SpringServer.domain.course.Course;
import ntcu.selab.SpringServer.web.dto.course.CourseDto;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseDto toDto(Course c) {
        if (c == null) return null;
        return new CourseDto(c.getClassId(), c.getName(), c.getSemester(), c.getTeacher());
    }
}
