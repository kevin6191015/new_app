package ntcu.selab.SpringServer.domain.course;
import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    
    @Id
    @Column(name = "class_id", nullable = false, length = 20)
    private String classId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    @Column(name = "teacher", nullable = false, length = 100)
    private String teacher; // 若資料表名稱是 teacher_name，請把 name 改成 "teacher_name"

    // ----- getters / setters -----
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
}
