package ru.authorization.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.mapper.UserMapper;

import java.util.Collection;

@Tag(name= "User Management Service", description = "API для менеджмента пользователей и аутенфикации")
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/create")
    @Operation(summary = "Создать нового пользователя", description = "Создаёт нового пользователя")
    public ResponseEntity<?> register(@RequestBody UserModel user) {

        var createdUser = userService.create(user);
        if (createdUser != null) {
            log.info(createdUser.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось создать пользователя");
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей", description = "возвращает список всех пользователей")
    public ResponseEntity<Iterable<UserDto>> getAllUsers() {

        Collection<UserDto> users = userService.getAll();
        return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
    }

    @GetMapping("/mail/{email}")
    @Operation(summary = "Получить пользователя по email", description = "возвращает пользователя по email")
    public ResponseEntity<?> getUserByEmail(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);
        var existingUserDto = UserMapper.mapToDto(userService.getByEmail(email));

        if (existingUserDto != null && isValidToken(token, existingUserDto)) {
            return ResponseEntity.ok(existingUserDto);
        }
        else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по id", description = "возвращает пользователя по id")
    public ResponseEntity<?> getUserById(@PathVariable long id) {

        var user = userService.getById(id);

        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Обновить данные пользователя по id", description = "Обновляет данные пользователя по id")
    public ResponseEntity<?> updateUserById(
            @PathVariable long id,
            @RequestBody UserModel user) {

        if (userService.updateById(id, user)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось обновить пользоватьские данные");
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить пользователя по id", description = "Удаляет пользователя по id")
    public ResponseEntity<?> deleteUserById(@PathVariable long id) {

        var result = userService.deleteById(id);
        if (result) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось удалить пользователя");
        }
    }

    @PostMapping("/token/validate")
    @Operation(summary = "Проверить токен", description = "Проверяет токен")
    private Boolean isValidToken(String token, UserDto userDto) {

        return tokenProvider.validateToken(token, userDto);
    }
}