package ru.authorization.auth.utils.security;

import org.mindrot.jbcrypt.BCrypt;

import ru.authorization.auth.utils.StaticResources;

public class PasswordHashing {

    private static final String salt = StaticResources.SALT_KEY;

    public String createPasswordHash(String password) {

        return BCrypt.hashpw(password, salt);
    }

    public boolean checkPasswordHash(String password, String hash) {

        return BCrypt.checkpw(password, hash);
    }
}
