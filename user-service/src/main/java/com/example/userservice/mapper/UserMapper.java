package com.example.userservice.mapper;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.model.User;
import com.example.userservice.service.ValidatorModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final ValidatorModel validator;

    public UserResponseDTO toUserResponseDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roleId(user.getRoleId())
                .roleName(user.getRoleName())
                .build();
    }

    public User toUser(UserRequestDTO userRequestDTO, UUID roleId) {
        return User.builder()
                .id(UUID.randomUUID())
                .username(userRequestDTO.getUsername())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .roleId(roleId)
                .newUser(true)
                .build();
    }

    public User update(Map<String, String> updates, User user) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "role" -> user.setRoleId(UUID.fromString(value));
                case "password" -> user.setPassword(passwordEncoder.encode(value));
                case "username" -> user.setUsername(value);
                default -> throw new IllegalArgumentException("no such attribute " + key);
            }
        });
        validator.validate(user);
        return user;
    }


}
