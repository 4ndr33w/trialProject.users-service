package ru.authorization.auth.testUtils;

import jakarta.inject.Inject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.authorization.auth.controllers.UserController;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.enums.UserStatus;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.security.PasswordHashing;

import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public abstract class TestUtils {
    @Mock
    protected UserModel actualUser;
    @Mock protected UserDto expectedUserDto;
    @Mock protected UserRepository userRepository;
    @InjectMocks
    protected UserService userService;
    @Mock protected PasswordHashing passwordHashing;
    @InjectMocks protected UserController userController;

    protected UserModel testUser  = new UserModel().builder()
            .email("test@test.ru")
            .password("test")
            .name("test")
            .lastName("test")
            .phone("test")
            .build();

    /*protected UserModel existingUser  = new UserModel().builder()
            .email("test@test.ru")
            .password("$2a$10$bynIPMKX68HSJHkaKAZ.mOx5ANbcXyAOpaT7AaUsHXRFv9prsDG22")
            .name("test")
            .lastName("test")
            .phone("test")
            .build();*/


    ///////////////////////////////////////////////////////////////////////
    //DTOS
    ///////////////////////////////////////////////////////////////////////

    protected  UserDto testUserDto0 = new UserDto().builder()
            .id(0)
            .name("test")
            .email("test@test.ru")
            .lastName("test")
            .phone("test")
            .created(new Date())
            .image(null)
            .userStatus(UserStatus.USER)
            .build();
    protected  UserDto testUserDto = new UserDto().builder()
            .id(1)
            .email("test@test.ru")
            .userStatus(UserStatus.USER)
            .name("test")
            .lastName("test")
            .build();
    protected  UserDto testUserDto1 = new UserDto().builder()
            .id(2)
            .email("test1@test.ru")
            .userStatus(UserStatus.USER)
            .name("test1")
            .lastName("test1")
            .build();
    protected  UserDto testUserDto2 = new UserDto().builder()
            .id(3)
            .email("test2@test.ru")
            .userStatus(UserStatus.USER)
            .name("test2")
            .lastName("test2")
            .build();

    ///////////////////////////////////////////////////////////////////////
    //MODELS
    ///////////////////////////////////////////////////////////////////////
    protected  UserModel testUser0 = new UserModel().builder()
            .id(0)
            .name("test")
            .email("test@test.ru")
            .password("$2a$10$bynIPMKX68HSJHkaKAZ.mOx5ANbcXyAOpaT7AaUsHXRFv9prsDG22")
            .lastName("test")
            .phone("test")
            .created(new Date())
            .updated(new Date())
            .image(null)
            .lastLoginDate(new Date())
            .userStatus(UserStatus.USER)
            .username(testUser.getEmail())
            .build();

    protected  UserModel testUser1 = new UserModel().builder()
            .id(1)
            .name("test1")
            .email("test1@test.ru")
            .password("test1")
            .lastName("test1")
            .phone("test1")
            .created(new Date())
            .updated(new Date())
            .image(null)
            .lastLoginDate(new Date())
            .userStatus(UserStatus.USER)
            .username(testUser.getEmail())
            .build();

    protected  UserModel testUpdatedUser0 = new UserModel().builder()
            .id(0)
            .name("testVasya")
            .email("test@test.ru")
            .password("$2a$10$bynIPMKX68HSJHkaKAZ.mOx5ANbcXyAOpaT7AaUsHXRFv9prsDG22")
            .lastName("Pupkin")
            .phone("test")
            .created(new Date())
            .updated(new Date())
            .image(null)
            .lastLoginDate(new Date())
            .userStatus(UserStatus.USER)
            .username(testUser.getEmail())
            .build();


    protected List<Object> getTestUsersCollention() {

        return  List.of(testUser0, testUser1/*, testUserDto2*/);//, testUserDto2);
    }
}
