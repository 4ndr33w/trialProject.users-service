package ru.authorization.auth.services;

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

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    public TokenService(UserRepository userRepository, JwtTokenProvider tokenProvider, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.tokenRepository = tokenRepository;
    }

    public TokenArchive saveToken(String token, long userId, String userEmail) {

        TokenArchive tokenArchive = new TokenArchive();
        Date expired = setExpirationDate();

        tokenArchive.setToken(token);
        tokenArchive.setUserid(userId);
        tokenArchive.setUsername(userEmail);
        tokenArchive.setCreated(new Date());
        tokenArchive.setExpired(expired);

        //log.info("TokenService.saveToken: " + token);
        return tokenRepository.save(tokenArchive);
    }

    private Date setExpirationDate() {

        Instant now = Instant.now();
        Instant newInstant = now.plus(StaticResources.JWT_EXPIRATION_MS, ChronoUnit.MILLIS);
        //log.info("TokenService.setExpirationDate: " + newInstant);
        return Date.from(newInstant);
    }

    public TokenArchive findTokenIfExist(String token) {

        //log.info("TokenService.findTokenIfExist: " + _token);
        return tokenRepository.findByToken(token);
        //log.info("Токен не найден: " + _token);
    }

    public Boolean delete(String token) {

        tokenRepository.deleteByToken(token);
        //log.info("TokenService.delete: " + token);
        return true;
    }

    public UserModel findUserByToken(String token) {

        String userName = tokenProvider.getUsernameFromToken(token);
        var userOptional = userRepository.findByUsername(userName);
        if(userOptional.isPresent()) {

            //log.info("TokenService.findUserByToken: " + userOptional.get());
            return userRepository.findByUsername(userName).get();
        }
        //log.info("Юзер по токену не найден. Токен: " + token);
        return null;
    }
}
