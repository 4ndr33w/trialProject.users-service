package ru.authorization.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.services.UserService;

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
    public Boolean register(@RequestBody UserModel user) {

        return userService.create(user);
    }

    @GetMapping
    public Iterable<UserDto> getAllUsers() {

        return userService.getAll();
    }

    @GetMapping("/mail/{email}")
    public ResponseEntity<?> getUserByEmail(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = authHeader.substring(7);
        var existingUser = userService.getByEmail(email);

        if (authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!isValidToken(token, existingUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (existingUser != null) {
            return ResponseEntity.ok(existingUser);
        }
        return null;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {

        return userService.getById(id);
    }
    @PutMapping("/update/{id}")
    public boolean updateUserById(
            @PathVariable long id,
            @RequestBody UserModel user) {

        return userService.updateById(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteUserById(@PathVariable long id) {

        return userService.deleteById(id);
    }

    private boolean isValidToken(String token, UserModel user) {

        return tokenProvider.validateToken(token, user);
         //token.startsWith("Bearer ");
    }
}