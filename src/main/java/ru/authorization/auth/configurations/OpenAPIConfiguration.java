package ru.authorization.auth.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    private final Environment environment;

    public OpenAPIConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI defineOpenAPI () {
        Server server = new Server();
        String serverUrl = environment.getProperty("api.server.url");
        server.setUrl(serverUrl);
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Имя Фамилия");
        myContact.setEmail("my.email@example.com");

        Info info = new Info()
                .title("Системное API для управления сотрудниками")
                .version("1.0")
                .description("Это API предоставляет эндпоинты для управления сотрудниками.")
                .contact(myContact);
        return new OpenAPI().info(info).servers(List.of(server));
    }
}