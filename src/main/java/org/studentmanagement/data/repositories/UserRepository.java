package org.studentmanagement.data.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.studentmanagement.data.entities.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Boolean existsByEmail(String email);
    Optional<UserEntity> findUserEntityByEmail(String email);
}
