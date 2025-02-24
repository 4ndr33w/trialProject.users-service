package ru.authorization.auth.controllers;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.services.UserServiceTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import ru.authorization.auth.testUtils.TestUtils;

public class UserControllerTests extends UserServiceTests {


    @Test
    public void testUpdateUserById_WhenUserUpdated_ReturnsOk() {

        when(userService.updateById(testUser0.getId(), testUser0)).thenReturn(true); // Мокируем успешное обновление

        // Act
        ResponseEntity<?> response = userController.updateUserById(testUser0.getId(), testUser0);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Проверяем статус 200 OK
        verify(userService, times(1)).updateById(testUser0.getId(), testUser0); // Проверяем, что метод сервиса вызван 1 раз
    }

    @Test
    public void testUpdateUserById_WhenUserNotUpdated_ReturnsBadRequest() {

        when(userService.updateById(userId, user)).thenReturn(false); // Мокируем неудачное обновление

        // Act
        ResponseEntity<?> response = userController.updateUserById(userId, user);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Проверяем статус 400 BAD REQUEST
        assertEquals("Не удалось обновить пользоватьские данные", response.getBody()); // Проверяем тело ответа
        verify(userService, times(1)).updateById(userId, user); // Проверяем, что метод сервиса вызван 1 раз
    }


}
