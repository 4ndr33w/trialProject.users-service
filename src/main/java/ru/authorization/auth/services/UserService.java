package ru.authorization.auth.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.utils.security.PasswordHashing;
import ru.authorization.auth.utils.exceptions.UserNotFoundException;
import ru.authorization.auth.utils.exceptions.EmailAlreadyBusyException;
import ru.authorization.auth.utils.exceptions.DatabaseTransactionException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Boolean create(UserModel user) {
        try {
            Optional<UserModel> userOptional = userRepository.findByEmail(user.getEmail());

            if (userOptional.isPresent()) {
                throw new EmailAlreadyBusyException(StaticResources.EMAIL_IS_ALREADY_IN_USE_EXCEPTION_MESSAGE);
            }

            String hashedPassword = PasswordHashing.createPasswordHash(user.getPassword());
            user.setPassword(hashedPassword);

            var savedUser = userRepository.save(user);

            if (savedUser.equals(user)) return true;
        } catch (Exception e) {
            throw new DatabaseTransactionException(StaticResources.CANNOT_CREATE_NEW_USER_EXCEPTION_MESSAGE);
        }
        return false;
    }

    public List<UserDto> getAll() {

        var users = userRepository.findAll();

        return users.stream().map(UserModel::toDto).toList();
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
            return userOptional.get().toDto();
        }
        else {
            String message = StaticResources.USER_NOT_FOUND_EXCEPTION_MESSAGE;
            throw new UserNotFoundException(message);
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
}
