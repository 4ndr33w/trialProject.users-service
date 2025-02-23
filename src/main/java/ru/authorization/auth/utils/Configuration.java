package ru.authorization.auth.utils;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    @Getter
    private static final Properties properties;

    static {
        try (InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("testConstantsRepository.properties")) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла констант", e);
        }
    }
}
