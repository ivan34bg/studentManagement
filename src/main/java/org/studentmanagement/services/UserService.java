package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.UserEntityUniqueConstraintViolationException;
import org.studentmanagement.exceptions.UserNotFoundException;

@Service
public interface UserService {
    UserViewModel registerUser(RegisterUserBindingModel userBindingModel) throws UserEntityUniqueConstraintViolationException, FieldConstraintViolationException;
    UserViewModel getUserByID(Long id) throws UserNotFoundException;
}
