package ru.authorization.auth.configurations;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.authorization.auth.components.CustomAuthenticationManager;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.controllers.UserController;
import ru.authorization.auth.repositories.TokenRepository;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.security.PasswordHashing;

@ExtendWith(MockitoExtension.class)
public abstract class ConfigurationTestsUtils {

    @Mock protected UserService userService;
    @Mock protected UserRepository userRepository;
    @Mock protected UserController userController;
    @Mock protected PasswordHashing passwordHashing;
    @Mock protected TokenRepository tokenRepository;
    @Mock protected JwtTokenProvider jwtTokenProvider;
    @Mock protected CustomAuthenticationManager authenticationManager;

    AuthenticationFilter filter = new AuthenticationFilter(userService, userRepository, tokenRepository, jwtTokenProvider, authenticationManager);

    protected String username = "testUser";
    protected String password = "testPassword";

    protected String validCredentials = username + ":" + password;
    protected String invalidCredentials = "invalidCredentials";

    protected String validBase64Credentials = java.util.Base64.getEncoder().encodeToString(validCredentials.getBytes());
    protected String invalidBase64Credentials = java.util.Base64.getEncoder().encodeToString(validCredentials.getBytes());

    protected String validAuthorizationHeaderValue = "Basic " + validBase64Credentials;
    protected String invalidAuthorizationHeaderValue = "Basic " + invalidBase64Credentials;

    protected String validAuthorizationHeader = "Authorization";
    protected String invalidAuthorizationHeader = "dSWfa3F35";


}