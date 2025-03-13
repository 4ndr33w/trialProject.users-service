package ru.authorization.auth.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.authorization.auth.models.TokenArchive;

@Repository
public interface TokenRepository extends CrudRepository<TokenArchive, Long> {

    @Query("SELECT t FROM TokenArchive t WHERE t.token = ?1")
    TokenArchive findByToken(String _token);

    @Modifying
    @Query("DELETE FROM TokenArchive t WHERE t.token = ?1")
    Boolean deleteByToken(String _token);
}
