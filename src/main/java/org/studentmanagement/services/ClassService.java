package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.RoleRequirementViolationException;

import java.security.Principal;
import java.util.List;

@Service
public interface ClassService {
    ClassViewModel addClass(AddClassBindingModel classBindingModel) throws FieldConstraintViolationException;
    List<ClassViewModel> getUserClasses(Principal principal) throws EntityNotFoundException;
    ClassViewModel getClass(Long id) throws EntityNotFoundException;
    ClassViewModel setTeacher(Long classId, Long teacherId)
            throws EntityNotFoundException, RoleRequirementViolationException;
    ClassViewModel addStudent(Long classId, Long studentId) throws EntityNotFoundException, RoleRequirementViolationException;
}
