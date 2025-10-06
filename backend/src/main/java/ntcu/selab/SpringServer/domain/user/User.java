package ntcu.selab.SpringServer.domain.user;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "role", nullable = false, length = 20)
    private String role; // student / TA / teacher / ROOT

    // ----- getters / setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // 小型工具方法（可選）
    @Transient
    public boolean isPrivileged() {
        return "teacher".equalsIgnoreCase(role) || "ROOT".equalsIgnoreCase(role);
    }

}
