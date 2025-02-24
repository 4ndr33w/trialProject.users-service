package ru.authorization.auth.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.utils.mapper.UserMapper;
import ru.authorization.auth.utils.security.PasswordHashing;
import ru.authorization.auth.utils.exceptions.UserNotFoundException;
import ru.authorization.auth.utils.exceptions.EmailAlreadyBusyException;
import ru.authorization.auth.utils.exceptions.DatabaseTransactionException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    //Для разрешения проблем с тестами поменял статические методы на нестатические
    //и создал локальный объект
    //имя переменной с больошой буквы, чтоб не было геморроя с переписыванием кода
    private PasswordHashing PasswordHashing = new PasswordHashing();

    public UserDto create(UserModel user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {

            log.info("Пользователь email: {} уже существует", user.getEmail());
            throw new EmailAlreadyBusyException(StaticResources.EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE);
        }
        String hashedPassword = PasswordHashing.createPasswordHash(user.getPassword());
        user.setUsername(user.getEmail());
        user.setPassword(hashedPassword);
        var savedUser = userRepository.save(user);

        if (savedUser.equals(user))
        {
            log.info("Пользователь email:  {} успешно создан", user.getEmail());
            return UserMapper.mapToDto(savedUser);
        }
        else {
            log.info("Пользователь email: {} не создан. Ошибка при сохранении: \n %s", user.getEmail(), StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE);
            throw new DatabaseTransactionException(StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE);
        }
    }

    public Collection<UserDto> getAll() {

        var users = userRepository.findAll();
        log.info("Все пользователи загружены");
        return users.stream().map(UserMapper::mapToDto).toList();
    }

    public UserModel getByEmail(String email) {

        var userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()) {
            log.info("Пользователь email: {} найден", email);
            return userOptional.get();
        }
        else {
            String message = StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE;
            log.info("Пользователь email: {} не найден", email);
            throw new UserNotFoundException(message);
        }
    }

    public UserDto getById(long id) {

        var userOptional = userRepository.findById(id);

        if(userOptional.isPresent()) {
            log.info("Пользователь id: {} найден", id);
            return UserMapper.mapToDto(userOptional.get());
        }
        else {
            log.info("Пользователь id: {} не найден", id);
            throw new UserNotFoundException(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Transactional
    public Boolean deleteById(long id) {

        var userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            log.info("Пользователь id: {} удалён", id);
            userRepository.delete(userOptional.get());
            return true;
        } else {
            String message = StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE;
            log.info("Пользователь id: {} не найден", id);
            throw new UserNotFoundException(message);
        }
    }

    @Transactional
    public Boolean updateById(long id, UserModel user) {

        var existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {
            var existingUser = existingUserOptional.get();
            userRepository.save(updatedUser(existingUser, user.getName(), user.getLastName(), user.getPhone(), user.getImage()));
            log.info("Пользователь id: {} обновлен", id);
            return true;
        }
        else {
            log.info("Пользователь id: {} не найден", id);
            throw new UserNotFoundException(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    private UserModel updatedUser(UserModel existingUser, String name, String lastName, String phone, byte[] image) {

        String newName = (name != null && !name.equals(existingUser.getName())) ? name : existingUser.getName();
        String newLastName = (lastName != null && !lastName.equals(existingUser.getLastName())) ? lastName : existingUser.getLastName();
        String newPhone = (phone != null && !phone.equals(existingUser.getPhone())) ? phone : existingUser.getPhone();
        byte[] newImage = (image != null && !Arrays.equals(image, existingUser.getImage())) ? image : existingUser.getImage();

        existingUser.setName(newName);
        existingUser.setLastName(newLastName);
        existingUser.setPhone(newPhone);
        existingUser.setImage(newImage);

        log.info("Поля пользователя id: {}, name: {}, lastName: {}, phone: {}, image: {} обновлены. Ожидает сохранения", existingUser.getId(), newName, newLastName, newPhone, newImage);
        return existingUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()) {
            log.info("Пользователь с email {} найден", username);
            return userOptional.get();
        }
        log.info("Пользователь c email {} не найден", username);
        throw new UsernameNotFoundException(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE);

    }
}
