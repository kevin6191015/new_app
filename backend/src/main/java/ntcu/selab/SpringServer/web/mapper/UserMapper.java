package ntcu.selab.SpringServer.web.mapper;
import ntcu.selab.SpringServer.domain.user.User;
import ntcu.selab.SpringServer.web.dto.user.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User u) {
        if (u == null) return null;
        return new UserDto(u.getId(), u.getName(), u.getRole());
    }
}