package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.RoleRequirementViolationException;

@Service
public interface ClassService {
    ClassViewModel addClass(AddClassBindingModel classBindingModel) throws FieldConstraintViolationException;
    ClassViewModel getClass(Long id) throws EntityNotFoundException;
    ClassViewModel setTeacher(Long classId, Long teacherId)
            throws EntityNotFoundException, RoleRequirementViolationException;
    ClassViewModel addStudent(Long classId, Long studentId) throws EntityNotFoundException, RoleRequirementViolationException;
}
