package ru.authorization.auth.services;

import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.enums.UserStatus;

public abstract class ServiceTestUtils {

    public UserModel testUser  = new UserModel().builder()
            .email("test@test.ru")
            .password("test")
            .name("test")
            .lastName("test")
            .phone("test")
            .build();

    public UserModel existingUser  = new UserModel().builder()
            .email("test@test.ru")
            .password("$2a$10$bynIPMKX68HSJHkaKAZ.mOx5ANbcXyAOpaT7AaUsHXRFv9prsDG22")
            .name("test")
            .lastName("test")
            .phone("test")
            .build();

    public  UserDto testUserDto = new UserDto().builder()
            .email("test@test.ru")
            .userStatus(UserStatus.USER)
            .name("test")
            .lastName("test")
            .build();

}
