package ru.authorization.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.AuthorizationUtils;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;

@Tag(name= "Контроллер пользователей", description = "REST API контроллер регистрации и авторизации пользователей")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthorizationUtils authorizationUtils;

    public UserController(UserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        authorizationUtils = new AuthorizationUtils(tokenProvider);
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Создаёт нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Не удалось создать пользователя")})
    public ResponseEntity<?> register(@RequestBody UserModel user) {

        var createdUser = userService.create(user);
        if (createdUser != null) {
            log.info("Пользователь создан: {}", createdUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось создать пользователя");
    }

    @GetMapping
    @Operation(summary = "Получить DTO пользователя по токену", description = "возвращает DTO пользователя по токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Не удалось получить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")})
    public ResponseEntity<?> getUserById(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userService.getUserDtoByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь не найден");
        }
        log.info("{} запросил UserDTO", username);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    @Operation(summary = "Обновить данные пользователя по токену", description = "Обновляет данные пользователя по токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Не удалось обновить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")})
    public ResponseEntity<?> updateUserById(
            @RequestBody UserModel user,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);
        var claims = authorizationUtils.validateTokenAndGetClaims(token);
        String username = authorizationUtils.getUserName(claims);

        if (!authorizationUtils.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var existingUser = userService.getUserDtoByEmail(username);
        if (userService.updateById(existingUser.getId(), user)) {
            log.info("{} обновил данные пользователя {}", username, user.getEmail());
            return ResponseEntity.ok().build();
        }
        log.info("Пользователь {} не обновлен", user.getEmail());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось обновить пользоватьские данные");
    }

    @DeleteMapping
    @Operation(summary = "Удалить пользователя по токену", description = "Удаляет пользователя по токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "400", description = "Не удалось удалить пользователя"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")})
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
            log.info("Удаление пользователя {}", username);
            return ResponseEntity.ok().build();
        }
        else {
            log.info("Пользователь {} не удален", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Не удалось удалить пользователя");
        }
    }
}
