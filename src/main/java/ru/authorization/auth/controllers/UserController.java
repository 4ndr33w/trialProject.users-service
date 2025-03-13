package ru.authorization.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.mapper.UserMapper;

import java.util.Collection;

@Tag(name= "User Management Service", description = "API для менеджмента пользователей и аутенфикации")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public UserController(UserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/create")
    @Operation(summary = "Создать нового пользователя", description = "Создаёт нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Пользователь создан"),
            @ApiResponse(responseCode = "409", description = "Не удалось создать пользователя")
    })
    public ResponseEntity<?> register(@RequestBody UserModel user) {

        var createdUser = userService.create(user);
        if (createdUser != null) {
            //log.info("Пользователь создан: {}", createdUser.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось создать пользователя");
    }

    @GetMapping
    @ApiResponse(responseCode = "200", description = "Список пользователей получен")
    @Operation(summary = "Получить список всех пользователей", description = "возвращает список всех пользователей")

    public ResponseEntity<Iterable<UserDto>> getAllUsers() {

        Collection<UserDto> users = userService.getAll();
        //log.info("Пользователи получены: {}", users.toString());
        return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
    }

    @GetMapping("/mail/{email}")
    @Operation(summary = "Получить пользователя по email", description = "возвращает пользователя по email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ закрыт")
    })
    public ResponseEntity<?> getUserByEmail(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);
        var existingUser = userService.getByEmail(email);

        if (!isValidToken(token, UserMapper.mapToDto(existingUser))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(existingUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по id", description = "возвращает пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Пользователь найден"),
            @ApiResponse(responseCode = "500", description = "Пользователь не найден")
    })
    public ResponseEntity<?> getUserById(@PathVariable long id) {

        var user = userService.getById(id);

        //log.info("Пользователь получен: {}", user.getEmail());
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Обновить данные пользователя по id", description = "Обновляет данные пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "500", description = "Пользователь не обновлен")
    })
    public ResponseEntity<?> updateUserById(
            @PathVariable long id,
            @RequestBody UserModel user) {

        if (userService.updateById(id, user)) {
            //log.info("Пользователь обновлен: {}", user.getEmail());
            return ResponseEntity.ok().build();
        }
        //log.info("Пользователь {} не обновлен", user.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось обновить пользоватьские данные");
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить пользователя по id", description = "Удаляет пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "500", description = "Пользователь не удалён")
    })
    public ResponseEntity<?> deleteUserById(@PathVariable long id) {

        var result = userService.deleteById(id);
        if (result) {
            //log.info("Пользователь удален: {}", id);
            return ResponseEntity.ok().build();
        }
        else {
            //log.info("Пользователь {} не удален", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось удалить пользователя");
        }
    }

    private Boolean isValidToken(String token, UserDto userDto) {

        //log.info("Проверка валидации токена: {}", token);
        return tokenProvider.validateToken(token, userDto);
    }
}