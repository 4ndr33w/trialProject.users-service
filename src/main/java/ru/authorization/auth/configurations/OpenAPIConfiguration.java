package ru.authorization.auth.configurations;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.authorization.auth.utils.StaticResources;

import java.util.List;

@Configuration
@AllArgsConstructor
public class OpenAPIConfiguration {

    private Environment environment;

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