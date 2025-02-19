package ru.authorization.auth.utils.security;

import org.mindrot.jbcrypt.BCrypt;

import ru.authorization.auth.utils.StaticResources;

public class PasswordHashing {

    private static String salt = StaticResources.SALT_KEY;

    public static String createPasswordHash(String password) {

        return BCrypt.hashpw(password, salt);
    }

    public static boolean checkPasswordHash(String password, String hash) {

        return BCrypt.checkpw(password, hash);
    }
}
