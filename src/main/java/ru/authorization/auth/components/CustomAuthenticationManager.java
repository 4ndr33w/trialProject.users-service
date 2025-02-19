package ru.authorization.auth.components;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.utils.security.PasswordHashing;

public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String password = authentication.getName();
        var user = (UserModel) authentication.getCredentials();

        boolean isPasswordValid = PasswordHashing.checkPasswordHash(password, user.getPassword());
        if (isPasswordValid) {

            var userStatus = user.getUserStatus();
            var authorities = List.of(new SimpleGrantedAuthority(userStatus.toString()));

            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }
        else  {
            throw new AuthenticationException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE) {
            };
        }
    }

}
