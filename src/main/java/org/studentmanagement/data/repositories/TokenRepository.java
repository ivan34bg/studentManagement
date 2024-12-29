package org.studentmanagement.data.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.studentmanagement.data.entities.TokenEntity;
import org.studentmanagement.data.entities.UserEntity;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, Long> {
    Optional<TokenEntity> findTokenEntityByTokenAndUserEmail(String token, String userEmail);
    Optional<TokenEntity> findTokenEntityByUser(UserEntity user);
    Optional<TokenEntity> findTokenEntityByToken(String token);
}
