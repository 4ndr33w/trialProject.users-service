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
//Надо, ё-моё, спрингСекуритям имплементировать чёртов UserDetailsService..
//и пофиг, что у меня есть поиск по емайлу
//НЕТ, ему нужен поиск по юзернейму
//и именно из UserDetailsService
//и именно переопределить его чёртов метод
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDto create(UserModel user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyBusyException(StaticResources.EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE);
        }
        String hashedPassword = PasswordHashing.createPasswordHash(user.getPassword());
        user.setUsername(user.getEmail());
        user.setPassword(hashedPassword);

        var savedUser = userRepository.save(user);

        if (savedUser.equals(user))
        {
            return UserMapper.mapToDto(savedUser);
        }
        else {
            throw new DatabaseTransactionException(StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE);
        }
    }

    public Collection<UserDto> getAll() {

        var users = userRepository.findAll();
        return users.stream().map(UserMapper::mapToDto).toList();
    }

    public UserModel getByEmail(String email) {

        var userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()) {
            return userOptional.get();
        }
        else {
            String message = StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE;
            throw new UserNotFoundException(message);
        }
    }

    public UserDto getById(long id) {

        var userOptional = userRepository.findById(id);

        if(userOptional.isPresent()) {
            return UserMapper.mapToDto(userOptional.get());
        }
        else {
            throw new UserNotFoundException(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Transactional
    public Boolean deleteById(long id) {
        var userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return true;
        } else {
            String message = StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE;
            throw new UserNotFoundException(message);
        }
    }

    @Transactional
    public Boolean updateById(long id, UserModel user) {

        var existingUserOptional = userRepository.findById(id);

        if (existingUserOptional.isPresent()) {

            var existingUser = existingUserOptional.get();

            userRepository.save(updatedUser(existingUser, user.getName(), user.getLastName(), user.getPhone(), user.getImage()));
            return true;
        }
        else throw new UserNotFoundException(StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE);
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

        return existingUser;
    }

    //вот она эта байда, ради которой имплементируем интерфейс
    //а потом ещё спрингСекурити ругается на модель юзера,
    //,дескать, и там надо исплементировать интерфейс
    //с никому нафиг не нужным полем @Override userName
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username).get();
    }
}
