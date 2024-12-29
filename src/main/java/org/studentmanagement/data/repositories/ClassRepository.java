package org.studentmanagement.data.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.studentmanagement.data.entities.ClassEntity;
import org.studentmanagement.data.entities.UserEntity;

import java.util.List;

@Repository
public interface ClassRepository extends CrudRepository<ClassEntity, Long> {
    List<ClassEntity> findAllByTeacher(UserEntity user);
    List<ClassEntity> findAllByStudentsContains(UserEntity user);
}
