package ntcu.selab.SpringServer.web.dto.auth;

import ntcu.selab.SpringServer.web.dto.user.UserDto;

public record LoginResponse(String token, UserDto user) {}
