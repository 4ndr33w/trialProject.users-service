package ru.authorization.auth.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import ru.authorization.auth.components.CustomAuthenticationManager;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.repositories.TokenRepository;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.services.UserService;
import ru.authorization.auth.utils.security.PasswordHashing;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private UserService UserService;
    //Для разрешения проблем с тестами поменял статические методы на нестатические
    //и создал локальный объект
    //имя переменной с больошой буквы, чтоб не было геморроя с переписыванием кода
    private PasswordHashing PasswordHashing = new PasswordHashing();
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationManager customAuthenticationManager) throws Exception {

        log.info("-------------------------\nЗаходим в: SecurityConfig.filterChain\n-------------------------");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //Здесь задаются условия фильтрации Эндпойнтов
                        //какие операции методы доступа по каким маршрутом дозволены без авторизации
                        //  '/*' - регламентирует один уровень вложенности: /users
                        // '/**' - регламентирует все уровни вложенности: /users/delete/{id}
                        //Если подключим зависимость springframework.security
                        //и не настроим цепочку фильтрации,
                        //то все запросы будут возвращать 401
                        //Если подключим зависимость springframework.security.web
                        //и не настроим цепочку фильтрации,
                        //то все запросы будут возвращать 403

                        // HTTPMethod можно не указывать - это необязательный параметр
                        //Так же тут можно задавать доступ по ролям
                        .requestMatchers(HttpMethod.GET,"/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        //.requestMatchers("/v3/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        //.requestMatchers("/swagger/**").permitAll()
                        .anyRequest().authenticated()
                )

                //httpBasic - тип аутенфикации. В данном случае у нас Basic Auth
                .httpBasic(withDefaults())
                .addFilter(new AuthenticationFilter(
                            userService,
                            userRepository,
                            tokenRepository,
                            new JwtTokenProvider(),
                            customAuthenticationManager))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        log.info("-------------------------\nВыходим из: SecurityConfig.filterChain\nНастроена фильтрация доступа\n-------------------------");
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        log.info("-------------------------\nЗаходим в: SecurityConfig.configure\nvoid: AuthenticationManagerBuilder запускает метод userDetailsService\nс параметром UserDervice\n-------------------------");
        auth.userDetailsService(UserService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                log.info("-------------------------\nfilterChain.encode\nХэшируем пароль\n-------------------------");
                return PasswordHashing.createPasswordHash((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                log.info("-------------------------\nfilterChain.encode\nПроверяем пароль\n-------------------------");
                return PasswordHashing.checkPasswordHash((String) rawPassword, encodedPassword);
            }
        };
    }
}