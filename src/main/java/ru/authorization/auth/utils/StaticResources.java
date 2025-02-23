package ru.authorization.auth.utils;

public class StaticResources {

    public static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 24;
    public static final String JWT_SECRET = Configuration.getProperties().getProperty("JWT_SECRET"); // "yourSecretKeyShouldBeLongerAndMoreComplexdwfFV we f34 F F#ER#WDDWr3tr ";

    public static final String EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE");
    public static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("USER_NOT_FOUND_EXCEPTION_MESSAGE");
    public static final String CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE");
    public static final String INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("INVALID_USERNAME_OR_PASSWORD_EXCEPTION_MESSAGE");
    public static final String USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE");
    public static final String BASIC_AUTHORIZATION_HEADER_IS_MISSING_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("BASIC_AUTHORIZATION_HEADER_IS_MISSING_EXCEPTION_MESSAGE");
    public static final String DATABASE_ACCESS_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("DATABASE_ACCESS_EXCEPTION_MESSAGE");
    public static final String BAD_REQUEST_EXCEPTION_MESSAGE = Configuration.getProperties().getProperty("BAD_REQUEST_EXCEPTION_MESSAGE");

    public static final String SALT_KEY = Configuration.getProperties().getProperty("SALT_KEY");
}
