package ru.authorization.auth.configurations;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.authorization.auth.models.Dtos.TokenDto;
import ru.authorization.auth.models.UserModel;

import ru.authorization.auth.services.UserService;
import ru.authorization.auth.services.TokenService;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.repositories.TokenRepository;
import ru.authorization.auth.components.CustomAuthenticationManager;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final TokenService tokenService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationManager authenticationManager;

    public AuthenticationFilter(UserService userService,
                                UserRepository userRepository,
                                TokenRepository tokenRepository,
                                JwtTokenProvider jwtTokenProvider,
                                CustomAuthenticationManager authenticationManager ) {

        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.tokenService = new TokenService(userRepository, jwtTokenProvider, tokenRepository);
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/login");
        log.info("AuthenticationFilter Constructor was called!");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        String authorizationHeader = req.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            var usernamePassword = getUsernamePasswordFromRequest(req);
            String login = usernamePassword[0];
            String enteredPassword = usernamePassword[1];

            var user = tryToGetUser(login);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(enteredPassword, user);

            log.info("attemptAuthentication method: Authentication request received!");
            return authenticationManager.authenticate(authenticationToken);
        }
        else {
            log.info("attemptAuthentication method: Authentication request is missing!");
            throw new IllegalArgumentException("Basic Authorization header is missing");
        }
    }

    public String[] getUsernamePasswordFromRequest(HttpServletRequest request){

        try {
            String base64Credentials = request.getHeader("Authorization").substring("Basic ".length());
            String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));

            var result = credentials.split(":", 2);

            log.info("getUsernamePasswordFromRequest method: Username and password retrieved successfully!");
            return credentials.split(":", 2);
        }
        catch (IllegalArgumentException e) {
            log.error("getUsernamePasswordFromRequest method: " + e.getMessage());
            throw new IllegalArgumentException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE);
        }
    }

    private UserModel tryToGetUser(String userName) {

        var user = userService.loadUserByUsername(userName);

        if (user != null) {
            log.info("tryToGetUser method: User found!");
            return (UserModel)user;
        }
        else {
            log.error("tryToGetUser method: User not found!");
            throw new IllegalArgumentException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authResult) throws IOException, ServletException {

        UserModel user = (UserModel) authResult.getPrincipal();
        String token = jwtTokenProvider.getToken(user);

        TokenDto tokenData = new TokenDto();
        tokenData.setToken(token);
        tokenData.setUserName(user.getUsername());
        tokenData.setUserStatus(user.getUserStatus());
        tokenData.setCreatedAt(new Date());

        StringWriter tokenSerializedData = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tokenSerializedData, tokenData);

        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");

        response.getWriter().write(tokenSerializedData.toString());
        log.info("successfulAuthentication method: Authentication successful!");
    }
}