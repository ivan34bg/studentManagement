package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.FieldConstraintViolationException;

@Service
public interface ClassService {
    ClassViewModel addClass(AddClassBindingModel classBindingModel) throws FieldConstraintViolationException;
    ClassViewModel getClassById(Long id);
}
