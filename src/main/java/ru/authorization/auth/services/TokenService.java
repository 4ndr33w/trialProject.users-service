package ru.authorization.auth.services;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.TokenArchive;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.repositories.TokenRepository;



import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    public TokenArchive saveToken(String token, long userId, String userEmail) {

        TokenArchive tokenArchive = new TokenArchive();
        Date expired = setExpirationDate();

        tokenArchive.setToken(token);
        tokenArchive.setUserid(userId);
        tokenArchive.setUsername(userEmail);
        tokenArchive.setCreated(new Date());
        tokenArchive.setExpired(expired);

        log.info("TokenService.saveToken: " + token);
        return tokenRepository.save(tokenArchive);
    }

    private Date setExpirationDate() {

        Instant now = Instant.now();
        Instant newInstant = now.plus(StaticResources.JWT_EXPIRATION_MS, ChronoUnit.MILLIS);
        log.info("TokenService.setExpirationDate: " + newInstant);
        return Date.from(newInstant);
    }

    public TokenArchive findTokenIfExist(String token) {

        var _token = tokenRepository.findByToken(token);
        if (_token != null) {
            log.info("TokenService.findTokenIfExist: " + _token);
            return _token;
        }
        log.info("Токен не найден: " + _token);
        return null;
    }

    public Boolean delete(String token) {

        tokenRepository.deleteByToken(token);
        log.info("TokenService.delete: " + token);
        return true;
    }

    public UserModel findUserByToken(String token) {

        String userName = tokenProvider.getUsernameFromToken(token);
        var userOptional = userRepository.findByUsername(userName);
        if(userOptional.isPresent()) {

            log.info("TokenService.findUserByToken: " + userOptional.get());
            return userRepository.findByUsername(userName).get();
        }
        log.info("Юзер по токену не найден. Токен: " + token);
        return null;
    }
}
