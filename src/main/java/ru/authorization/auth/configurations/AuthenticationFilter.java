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
import ru.authorization.auth.components.CustomAuthenticationManager;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.repositories.TokenRepository;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.services.TokenService;
import ru.authorization.auth.utils.StaticResources;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationManager authenticationManager;

    public AuthenticationFilter(CustomAuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, TokenRepository tokenRepository) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.tokenService = new TokenService(tokenRepository, jwtTokenProvider, userRepository);

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

    private String[] getUsernamePasswordFromRequest(HttpServletRequest request){

        String base64Credentials = request.getHeader("Authorization").substring("Basic ".length());
        String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));

        var result = credentials.split(":", 2);

        if (result[0] == null || result[1] == null) {
            throw new IllegalArgumentException(StaticResources.USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE);
        }
        return credentials.split(":", 2);
    }

    private UserModel tryToGetUser(String userName) {

        var userOptional = userRepository.findByUsername(userName);

        if (userOptional.isPresent()) {
            return userOptional.get();
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