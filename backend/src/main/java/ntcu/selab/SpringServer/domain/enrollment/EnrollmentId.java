package ntcu.selab.SpringServer.domain.enrollment;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * 複合主鍵 (user_id, class_id)：
 * - 必須實作 Serializable
 * - 必須正確覆寫 equals/hashCode（基於所有主鍵欄位）
 */
@Embeddable
public class EnrollmentId implements Serializable{
    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "class_id", nullable = false, length = 20)
    private String classId;

    public EnrollmentId() { }

    public EnrollmentId(String userId, String classId) {
        this.userId = userId;
        this.classId = classId;
    }

    public String getUserId() { return userId; }
    public String getClassId() { return classId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setClassId(String classId) { this.classId = classId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollmentId that)) return false;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, classId);
    }
}
