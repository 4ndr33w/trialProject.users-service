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

    //Для разрешения проблем с тестами поменял статические методы на нестатические
    //и создал локальный объект
    //имя переменной с больошой буквы, чтоб не было геморроя с переписыванием кода
    private PasswordHashing PasswordHashing = new PasswordHashing();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info("-------------------------\nCustomAuthenticationManager.authenticate\nВход в метод authenticate\n-------------------------");
        String password = authentication.getName();
        var user = (UserModel) authentication.getCredentials();

        boolean isPasswordValid = PasswordHashing.checkPasswordHash(password, user.getPassword());
        if (isPasswordValid) {

            var userStatus = user.getUserStatus();
            var authorities = List.of(new SimpleGrantedAuthority(userStatus.toString()));

            log.info("-------------------------\nCustomAuthenticationManager.authenticate\nUser: " + user.getUsername() + " has been authenticated\n-------------------------");
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }
        else  {
            log.info("-------------------------\nCustomAuthenticationManager.authenticate\nUser: " + user.getUsername() + " has not been authenticated\n-------------------------");
            throw new AuthenticationException(StaticResources.INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE) {
            };
        }
    }

}
