package ru.authorization.auth.services;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.authorization.auth.repositories.UserRepository;


//И реализацию этой хренотени надо написать
//Переопределить UserDetaiilsService
//Записать свою логику реализации
//Хотя, вот сейчас пишу эти строки
//и понямаю, что можно было
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username).get();
    }
}
