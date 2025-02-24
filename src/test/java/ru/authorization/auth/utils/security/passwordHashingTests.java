package ru.authorization.auth.utils.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class passwordHashingTests {

    //Для разрешения проблем с тестами поменял статические методы на нестатические
    //и создал локальный объект
    //имя переменной с больошой буквы, чтоб не было геморроя с переписыванием кода
    private PasswordHashing PasswordHashing = new PasswordHashing();
    String password = "123";
    String passwordHash = "$2a$10$bynIPMKX68HSJHkaKAZ.mOx5ANbcXyAOpaT7AaUsHXRFv9prsDG22";
    public passwordHashingTests() {

    }
    @Test
    @DisplayName("Test verifying password and hash expects succeed")
    public void checkPasswordHashTestExpectSuccess() {

        boolean isPasswordValid = PasswordHashing.checkPasswordHash(password, passwordHash);
        assertTrue(isPasswordValid);
    }

    @Test
    @DisplayName("Test verifying password and hash expect fail")
    public void checkPasswordHashTestExpectFail() {

        boolean isPasswordValid = PasswordHashing.checkPasswordHash("passw0rd", passwordHash );
        assertFalse(isPasswordValid);
    }

    @Test
    @DisplayName("Test hashing password with success result")
    public void createPasswordHashTestExpextSuccess() {

        String getPasswordHash = PasswordHashing.createPasswordHash("123");

        assertEquals(passwordHash, getPasswordHash);

        System.out.println(getPasswordHash);
    }

    @Test
    @DisplayName("Test hashing password expect fail")
    public void createPasswordHashTestExpectFail() {

        String getPasswordHash = PasswordHashing.createPasswordHash("passw0rd");

        assertNotEquals("passw0rd", "%2a$10$bynIPMKX68HSJHkaKAZ.mOw7WG6NcitXGxTcHpqxJIPMXa1dmUIJe");
    }
}
