package ru.authorization.auth.components;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.utils.security.PasswordHashing;

@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String password = authentication.getName();
        var user = (UserModel) authentication.getCredentials();

        boolean isPasswordValid = PasswordHashing.checkPasswordHash(password, user.getPassword());
        if (isPasswordValid) {

            var userStatus = user.getUserStatus();
            var authorities = List.of(new SimpleGrantedAuthority(userStatus.toString()));

            log.info("CustomAuthenticationManager.authenticate\nUser: " + user.getUsername() + " has been authenticated");
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }
        else  {
            log.info("CustomAuthenticationManager.authenticate\nUser: " + user.getUsername() + " has not been authenticated");
            throw new AuthenticationException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE) {
            };
        }
    }

}
