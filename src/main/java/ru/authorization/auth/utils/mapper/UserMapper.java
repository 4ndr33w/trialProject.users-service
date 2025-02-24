package ru.authorization.auth.utils.mapper;

import org.springframework.stereotype.Component;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;

import java.util.Date;

@Component
public class UserMapper {

    private UserMapper() {}

    public static UserDto mapToDto(UserModel model) {
        return UserDto.builder()
                .id(model.getId())
                .name(model.getName())
                .lastName(model.getLastName())
                .email(model.getEmail())
                .phone(model.getPhone())
                .image(model.getImage())
                .created(model.getCreated())
                .userStatus(model.getUserStatus())
                .build();
    }

    public static UserModel mapToModel(UserDto dto, String password, Date updated, Date lastLoginDate) {
        return UserModel.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(password)
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .created(dto.getCreated())
                .updated(updated)
                .image(dto.getImage())
                .lastLoginDate(lastLoginDate)
                .userStatus(dto.getUserStatus())
                .username(dto.getEmail())
                .build();
    }
}