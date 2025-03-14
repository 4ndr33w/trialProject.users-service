package ru.authorization.auth.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.AuthorizationUtils;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthorizationUtils authorizationUtils;

    public AdminController(UserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        authorizationUtils = new AuthorizationUtils(tokenProvider);
    }

    @Tag(name = "Контроллер администрирования: Get", description = "GET запросы контроллера администрирования")
    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "204", description = "Список пользователей пуст"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")
    })
    @Operation(summary = "Получить список всех пользователей", description = "возвращает список всех пользователей")

    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if(authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var users = userService.getAll();

        if(users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        log.info("{} запросил список всех пользователей", username);
        return ResponseEntity.ok(users);
    }

    @Tag(name = "Контроллер администрирования: Get", description = "GET запросы контроллера администрирования")
    @GetMapping("/mail/{email}")
    @Operation(summary = "Получить пользователя по email", description = "возвращает пользователя по email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Не удалось найти пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")
    })
    public ResponseEntity<?> getUserByEmail(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if(authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(authHeader.substring(7));
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            log.error("Ошибка авторизации пользователя {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("{} запросил DTO пользователя {}", username, email);
        var existingUser = userService.getUserDtoByEmail(email);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь не найден");
        }
        return ResponseEntity.ok(existingUser);
    }

    @Tag(name = "Контроллер администрирования")
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по id", description = "возвращает пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Не удалось получить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")
    })
    public ResponseEntity<?> getUserById(@PathVariable long id,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var user = userService.getById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь не найден");
        }
        log.info("{} запросил DTO пользователя {} по id {}", username, user.getEmail(), user.getId());
        return ResponseEntity.ok(user);
    }

    @Tag(name = "Контроллер администрирования")
    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные пользователя по id", description = "Обновляет данные пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Не удалось обновить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")
    })
    public ResponseEntity<?> updateUserById(
            @PathVariable long id,
            @RequestBody UserModel user,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (userService.updateById(id, user)) {
            log.info("{} обновил данные пользователя {}", username, user.getEmail());
            return ResponseEntity.ok().build();
        }
        log.info("Пользователь {} не обновлен", user.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось обновить пользоватьские данные");
    }

    @Tag(name = "Контроллер администрирования")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по id", description = "Удаляет пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "400", description = "Не удалось удалить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")
    })
    public ResponseEntity<?> deleteUserById(@PathVariable long id,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var result = userService.deleteById(id);
        if (result) {
            log.info("{} удалил пользователя {}", username, id);
            return ResponseEntity.ok().build();
        }
        else {
            log.info("Пользователь {} не удален", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось удалить пользователя");
        }
    }

    @Tag(name = "Контроллер администрирования: Change", description = "Изменить параметр пользователя")
    @PutMapping("/change/email/{id}")
    @Operation(summary = "Изменить почту пользователя по id", description = "Изменяет почту пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Не удалось обновить пользователя, или email уже занят"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")
    })
    public ResponseEntity<?> updateUserEmail(@PathVariable long id,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader,
                                             @RequestBody UserModel userModel) {
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var existingUserWithPointedEmail = userService.getUserDtoByEmail(userModel.getEmail());
        if(existingUserWithPointedEmail != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с таким email уже существует");
        }
        var result = userService.changeUserMail(id, userModel);
        if (result == null) {
            log.info("Не удалось поменять почту у пользователя {}", userModel.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось изменить почту пользователя");
        }
        log.info("{} поменял почту пользователя {} на {}", username, userModel.getEmail(), result.getEmail());
        return ResponseEntity.ok(result);
    }

    @Tag(name = "Контроллер администрирования: Change", description = "Изменить параметр пользователя")
    @PutMapping("/change/role/{id}")
    @Operation(summary = "Изменить роль пользователя по id", description = "Изменяет роль пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Не удалось обновить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")})
    public ResponseEntity<?> updateRole(@PathVariable long id,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @RequestBody UserModel userModel) {
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var result = userService.changeUserStatus(id, userModel);
        if (result == null) {
            log.info("Не удалось поменять роль у пользователя");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось обновить роль пользователя");
        }
        log.info("{} поменял роль пользователя {} c {} на {}", username, result.getEmail(), userModel.getUserStatus(), result.getUserStatus());
        return ResponseEntity.ok(result);
    }

    @Tag(name = "Контроллер администрирования: Change", description = "Изменить параметр пользователя")
    @PutMapping("/change/password/{id}")
    @Operation(summary = "Изменить пароль пользователя по id", description = "Изменяет пароль пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль обновлен"),
            @ApiResponse(responseCode = "400", description = "Не удалось обновить пароль"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав доступа")})
    public ResponseEntity<?> updateUserPassword(@PathVariable long id,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader,
                                                @RequestBody UserModel userModel) {
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authorizationUtils.isAdmin(claims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var result = userService.changeUserPassword(id, userModel);
        if (result == null) {
            log.info("Не удалось поменять пароль у пользователя");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось обновить пароль пользователя");

        }
        log.info("{} поменял пароль пользователя {} ", username, userModel.getEmail());
        return ResponseEntity.ok(result);
    }
}