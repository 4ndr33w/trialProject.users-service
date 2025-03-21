package ru.authorization.auth.configurations;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

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

            return authenticationManager.authenticate(authenticationToken);
        }
        else {
            throw new IllegalArgumentException("Basic Authorization header is missing");
        }
    }

    public String[] getUsernamePasswordFromRequest(HttpServletRequest request){

        try {
            String base64Credentials = request.getHeader("Authorization").substring("Basic ".length());
            String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));

            var result = credentials.split(":", 2);

            return credentials.split(":", 2);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE);
            //return null;
        }

    }

    private UserModel tryToGetUser(String userName) {

        var user = userService.loadUserByUsername(userName);

        if (user != null) {
            return (UserModel)user;
        }
        else {
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

        var tokenSaving = tokenService.saveToken(token, user.getId(), user.getEmail());
        response.addHeader("Authorization", "Bearer " + token);
        response.getWriter().write("Authentication successful. Token: " + token);
    }
}