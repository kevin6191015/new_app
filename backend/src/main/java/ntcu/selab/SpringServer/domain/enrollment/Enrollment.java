package ntcu.selab.SpringServer.domain.enrollment;
import jakarta.persistence.*;
import ntcu.selab.SpringServer.domain.course.Course;
import ntcu.selab.SpringServer.domain.user.User;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @EmbeddedId
    private EnrollmentId id; // 複合主鍵：user_id + class_id

    @MapsId("userId")
    @ManyToOne(fetch = LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("classId")
    @ManyToOne(fetch = LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @JoinColumn(name = "class_id", nullable = false)
    private Course course;

    @Column(name = "role_in_course", length = 20)
    private String roleInCourse; // student / TA / teacher / ROOT（與實際需求一致即可）

    public Enrollment() { }

    public Enrollment(User user, Course course, String roleInCourse) {
        this.user = user;
        this.course = course;
        this.roleInCourse = roleInCourse;
        this.id = new EnrollmentId(user.getId(), course.getClassId());
    }

    // ----- getters / setters -----
    public EnrollmentId getId() { return id; }
    public void setId(EnrollmentId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (this.id == null) this.id = new EnrollmentId();
        this.id.setUserId(user != null ? user.getId() : null);
    }

    public Course getCourse() { return course; }
    public void setCourse(Course course) {
        this.course = course;
        if (this.id == null) this.id = new EnrollmentId();
        this.id.setClassId(course != null ? course.getClassId() : null);
    }

    public String getRoleInCourse() { return roleInCourse; }
    public void setRoleInCourse(String roleInCourse) { this.roleInCourse = roleInCourse; }
    
}
