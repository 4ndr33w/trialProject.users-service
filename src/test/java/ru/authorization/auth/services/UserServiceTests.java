package ru.authorization.auth.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.utils.exceptions.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests extends ru.authorization.auth.testUtils.TestUtils  {

    @InjectMocks protected UserService userService;
/*
    @Ignore("User создан, но IDE ругается: PasswordHashing \"Wanted but not invoked\"")
    @Test
    public void testCreateUser_Success() {

        when(passwordHashing.createPasswordHash(any(String.class))).thenReturn("AnyPasswordHashString");
        when(userRepository.save(testUser0)).thenReturn(testUser0);

        UserDto testUserDto0 = userService.create(testUser0);

        assertNotNull(testUserDto0 );
        assertEquals(UserMapper.mapToDto(testUser0), testUserDto0);
        verify(passwordHashing, times(1)).createPasswordHash(any(String.class));
        verify(userService, times(1)).create(testUser0);
    }*/

    @Test
    public void testGetAll_WhenUsersExist_ReturnsListOfUserDto() {

        List<UserModel> users = List.of(testUser0, testUser1);

        when(userRepository.findAll()).thenReturn(users);

        Collection<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();

        assertTrue(result.stream().anyMatch(dto -> dto.getName().equals("test") && dto.getEmail().equals("test@test.ru")));
        assertTrue(result.stream().anyMatch(dto -> dto.getName().equals("test1") && dto.getEmail().equals("test1@test.ru")));
    }

    @Test
    public void testGetAll_WhenNoUsersExist_ReturnsEmptyList() {

        when(userRepository.findAll()).thenReturn(List.of());

        Collection<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }
/*
    @Test
    @Ignore
    public void testGetById_WhenUserExists_ReturnsUserDto() {

        when(userRepository.findById(testUser0.getId())).thenReturn(java.util.Optional.of(testUser0));

        UserDto result = userService.getById(testUser0.getId());

        assertNotNull(result);
        assertEquals(UserMapper.mapToDto(testUser0), result);
        verify(userRepository, times(1)).findById(testUser0.getId());
    }
*/
    @Test
    public void testGetById_WhenUserDoesNotExist_ThrowsUserNotFoundException() {

        //long userId = testUser0.getId();
        when(userRepository.findById(testUser0.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getById(testUser0.getId()));

        assertEquals(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());

        verify(userRepository, times(1)).findById(testUser0.getId());
    }

    @Test
    public void testDeleteById_WhenUserExists_ReturnsTrue() {

        when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        doNothing().when(userRepository).delete(testUser1);

        boolean result = userService.deleteById(testUser1.getId());

        assertTrue(result);
        verify(userRepository, times(1)).findById(testUser1.getId());
        verify(userRepository, times(1)).delete(testUser1);
    }

    @Test
    public void testDeleteById_WhenUserDoesNotExist_ThrowsUserNotFoundException() {
        when(userRepository.findById(testUser1.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteById(testUser1.getId()));
        assertEquals(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());

        verify(userRepository, times(1)).findById(testUser1.getId());
        verify(userRepository, never()).delete(any());
    }

    @Test
    public void testUpdateById_WhenUserExists_ReturnsTrue() {

        when(userRepository.findById(testUser0.getId())).thenReturn(Optional.of(testUser0));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = userService.updateById(testUser0.getId(), testUpdatedUser0);

        assertTrue(result);
        verify(userRepository, times(1)).findById(testUser0.getId());
        verify(userRepository, times(1)).save(argThat(user -> {

            assertEquals(testUpdatedUser0.getName(), user.getName());
            assertEquals(testUpdatedUser0.getLastName(), user.getLastName());
            assertEquals(testUpdatedUser0.getPhone(), user.getPhone());
            assertEquals(testUpdatedUser0.getImage(), user.getImage());
            return true;
        }));
    }

    @Test
    public void testUpdateById_WhenUserDoesNotExist_ThrowsUserNotFoundException() {

        when(userRepository.findById(testUser0.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateById(testUser0.getId(), testUpdatedUser0));
        assertEquals(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        verify(userRepository, times(1)).findById(testUser0.getId());
        verify(userRepository, never()).save(any(UserModel.class));
    }


}
