package ru.authorization.auth.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Properties properties;

    static {
        try (InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("testConstantsRepository.properties")) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Ошибка при загрузке файла констант", e);
            throw new RuntimeException("Ошибка при загрузке файла констант", e);
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
