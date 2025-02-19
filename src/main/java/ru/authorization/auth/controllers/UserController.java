package ru.authorization.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/hello")
    public String hello() {
        return "Hello from User Controller!";
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
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            Authentication authentication) {

        if (authentication == null || authHeader.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isValidToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return null;
        //return userService.getByEmail(email);
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

    private boolean isValidToken(String authHeader) {

        return authHeader.startsWith("Bearer ");
    }
}
