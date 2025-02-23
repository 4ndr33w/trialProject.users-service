package ru.authorization.auth.configurations;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.authorization.auth.models.UserModel;

import ru.authorization.auth.services.UserService;
import ru.authorization.auth.services.TokenService;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.repositories.TokenRepository;
import ru.authorization.auth.components.CustomAuthenticationManager;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenService tokenService;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationManager authenticationManager;

    public AuthenticationFilter(UserService userService,
                                UserRepository userRepository,
                                TokenRepository tokenRepository,
                                JwtTokenProvider jwtTokenProvider,
                                CustomAuthenticationManager authenticationManager ) {

        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.tokenService = new TokenService(userRepository, jwtTokenProvider, tokenRepository);

        setAuthenticationManager(authenticationManager);

        //точка входа, с которой перехватываются запросы на авторизацию
        //вызывается спрингом без нашего участия / желания / согласия
        //сразу при подключении springframework.security
        //по этому аннотацию @RequiredArgConstructor не применяю
        setFilterProcessesUrl("/login");
        log.info("Конструктор класса AuthenticationFilter");
    }

    // Если не переопределить этот метод,
    // то будет вызываться метод attemptAuthentication() из класса UsernamePasswordAuthenticationFilter
    // который нам запорет всю аутенфикацию
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        String authorizationHeader = req.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {

            var usernamePassword = getUsernamePasswordFromRequest(req);
            String login = usernamePassword[0];
            String enteredPassword = usernamePassword[1];

            var user = tryToGetUser(login);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(enteredPassword, user);

            log.info("Пользователь " + login + " попытка аутенфикации");
            return authenticationManager.authenticate(authenticationToken);
        }
        else {
            log.info("Не удалось получить Basic Authorization header");
            throw new IllegalArgumentException("Basic Authorization header is missing");
        }
    }

    private String[] getUsernamePasswordFromRequest(HttpServletRequest request){

        String base64Credentials = request.getHeader("Authorization").substring("Basic ".length());
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));

        var result = credentials.split(":", 2);

        if (result[0] == null || result[1] == null) {
            log.info("Не удалось получить Basic Authorization header");
            throw new IllegalArgumentException(StaticResources.USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE);
        }
        log.info("Пользователь " + result[0] + " получаем пароль и логин");
        return credentials.split(":", 2);
    }

    private UserModel tryToGetUser(String userName) {

        var user = userService.loadUserByUsername(userName);

        if (user != null) {
            log.info("Пользователь " + userName + " найден");
            return (UserModel)user;
        }
        else {
            log.info("Пользователь " + userName + " не найден");
            throw new IllegalArgumentException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE);
        }
    }

    //Из названия всё ясно
    //Прописываем свою логику при успешной аутенфикации
    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authResult) throws IOException, ServletException {

        UserModel user = (UserModel) authResult.getPrincipal();
        String token = jwtTokenProvider.getToken(user);

        var tokenSaving = tokenService.saveToken(token, user.getId(), user.getEmail());

        log.info("Пользователь " + user.getUsername() + " успешно аутенфицирован Отправляю токен: \n" + token);
        response.addHeader("Authorization", "Bearer " + token);
        response.getWriter().write("Authentication successful. Token: " + token);
    }
}