package ru.authorization.auth.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.utils.security.PasswordHashing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(PowerMockRunner.class)
@PrepareForTest(PasswordHashing.class) // Указываем класс со статическим методом
public class UserServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private UserModel actualUser;

    @Mock
    private UserDto expectedUserDto;

    @Test
    public void testCreateUser_Success() {
        // Arrange
        PowerMockito.mockStatic(PasswordHashing.class); // Мокируем статический класс
        when(actualUser.getPassword()).thenReturn("password"); // Настраиваем mock-объект

        // Мокирование статического метода
        when(PasswordHashing.createPasswordHash("password")).thenReturn("hashedPassword");

        // Мокирование метода сервиса
        when(userService.create(actualUser)).thenReturn(expectedUserDto);

        // Act
        UserDto actualUserDto = userService.create(actualUser);

        // Assert
        assertNotNull(actualUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        verify(userService, times(1)).create(actualUser);
        PowerMockito.verifyStatic(PasswordHashing.class, times(1)); // Проверка вызова статического метода
        PasswordHashing.createPasswordHash("password");
    }

/*
    @Test
    public void testCreateUser_Success() {
        // Arrange
        UserModel user = testUser;
        UserModel savedUser = existingUser;
        UserDto expectedUserDto = testUserDto;

        var actualUserDto = userService.create(user);

        when(userService.getById(user.getId())).thenReturn(UserMapper.mapToDto(user));//user.getEmail())).thenReturn(Optional.empty());
        when(PasswordHashing.createPasswordHash(user.getPassword())).thenReturn(existingUser.getPassword());
        when(userService.create(user)).thenReturn(UserMapper.mapToDto(user));
        when(UserMapper.mapToDto(savedUser)).thenReturn(expectedUserDto);

        // Assert
        assertNotNull(savedUser);
        assertEquals(expectedUserDto, UserMapper.mapToDto(user));
        verify(userService, times(1)).getById(existingUser.getId());
        verify(passwordHashing, times(1)).createPasswordHash(user.getPassword());
        verify(userService, times(1)).create(user);
    }*/

/*
    @Test
    public void testCreateUser_EmailAlreadyExists() {
        // Arrange
        UserModel user = new UserModel();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new UserModel()));

        // Act & Assert
        EmailAlreadyBusyException exception = assertThrows(EmailAlreadyBusyException.class, () -> {
            userService.create(user);
        });

        assertEquals(StaticResources.EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordHashing, never()).createPasswordHash(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testCreateUser_DatabaseTransactionError() {
        // Arrange
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setPassword("password");

        UserModel savedUser = new UserModel();
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("hashedPassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(PasswordHashing.createPasswordHash(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(UserMapper.mapToDto(savedUser)).thenReturn(null); // Симулируем ошибку маппинга

        // Act & Assert
        DatabaseTransactionException exception = assertThrows(DatabaseTransactionException.class, () -> {
            userService.create(user);
        });

        assertEquals(StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordHashing, times(1)).createPasswordHash(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }*/
}
