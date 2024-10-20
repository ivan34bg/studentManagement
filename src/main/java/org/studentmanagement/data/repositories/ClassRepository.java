package org.studentmanagement.data.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.studentmanagement.data.entities.ClassEntity;

@Repository
public interface ClassRepository extends CrudRepository<ClassEntity, Integer> {
}
