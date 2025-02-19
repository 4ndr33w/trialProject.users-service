package ru.authorization.auth.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.authorization.auth.models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    public Optional<UserModel> findByEmail(String email);

    public Optional<UserModel> findByUsername(String email);
}
