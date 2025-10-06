package ntcu.selab.SpringServer.web.dto.course;

public record CourseDto(
        String classId,
        String name,
        String semester,
        String teacher
) {}
