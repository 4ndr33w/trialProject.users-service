package ru.authorization.auth.services;

import org.springframework.stereotype.Service;
import ru.authorization.auth.repositories.UserRepository;

@Service
public class TokenService {

    private final UserRepository userRepository;

    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
