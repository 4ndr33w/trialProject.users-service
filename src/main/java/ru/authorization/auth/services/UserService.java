package ru.authorization.auth.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;
import ru.authorization.auth.utils.mapper.UserMapper;
import ru.authorization.auth.utils.security.PasswordHashing;
import ru.authorization.auth.utils.exceptions.UserNotFoundException;
import ru.authorization.auth.utils.exceptions.EmailAlreadyBusyException;
import ru.authorization.auth.utils.exceptions.DatabaseTransactionException;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final UserRepository userRepository;
    private final PasswordHashing PasswordHashing;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.PasswordHashing = new PasswordHashing();
    }

    public UserDto create(UserModel user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {

            log.error("Пользователь email: {} уже существует", user.getEmail());
            throw new EmailAlreadyBusyException(StaticResources.EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE);
        }
        String hashedPassword = PasswordHashing.createPasswordHash(user.getPassword());
        user.setUsername(user.getEmail());
        user.setPassword(hashedPassword);
        var savedUser = userRepository.save(user);

        if (savedUser.equals(user))
        {
            log.info("Пользователь email: {} успешно создан", user.getEmail());
            return UserMapper.mapToDto(savedUser);
        }
        else {
            log.info("Пользователь email: {} не создан. Ошибка при сохранении: \n {}", user.getEmail(), StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE);
            throw new DatabaseTransactionException(StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE);
        }
    }

    public Collection<UserDto> getAll() {

        var users = userRepository.findAll();
        log.info("Все пользователи загружены");
        return users.stream().map(UserMapper::mapToDto).toList();
    }

    public Optional<UserModel> getUserModelByEmail(String email) {

        var userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()) {
            log.info("Пользователь email: {} найден", email);
        }
        else {
            String message = StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE;
            log.info("Пользователь email: {} не найден", email);
        }
        return userOptional;
    }
    public UserDto getUserDtoByEmail(String email) {
        var userOptional = getUserModelByEmail(email);
        return userOptional.map(UserMapper::mapToDto).orElse(null);
    }

    public UserDto getById(long id) {

        var userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) {

            log.info("Пользователь id: {} не найден", id);
            return null;
        }
        else {
            log.info("Пользователь id: {} найден", id);
            return UserMapper.mapToDto(userOptional.get());
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
            log.info("Пользователь id: {} не найден", id);
            return false;
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
            log.info("Пользователь id: {} не найден, либо не обновлён", id);
            return false;
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

    public UserDto changeUserMail(long id, UserModel user) {
        var existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            existingUser.get().setEmail(user.getEmail());
            existingUser.get().setUsername(user.getEmail());
            userRepository.save(existingUser.get());
            return UserMapper.mapToDto(existingUser.get());
        }
        else {
            log.info("Пользователь id: {} не найден", user.getId());
            return null;
        }
    }

    public UserDto changeUserPassword(long id, UserModel user) {
        var existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            var hashedPassword = PasswordHashing.createPasswordHash(user.getPassword());
            existingUser.get().setPassword(hashedPassword);
            userRepository.save(existingUser.get());
            return UserMapper.mapToDto(existingUser.get());
        }
        else {
            log.info("Пользователь id: {} не найден", user.getId());
            return null;
        }
    }
    public UserModel changeUserStatus(long id, UserModel user) {
        var existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            existingUser.get().setUserStatus(user.getUserStatus());
            userRepository.save(existingUser.get());
            return existingUser.get();
        }
        else {
            log.info("Пользователь id: {} не найден", user.getId());
            return null;
        }
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
