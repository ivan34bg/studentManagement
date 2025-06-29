package org.studentmanagement.services;

import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.LoginBindingModel;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.viewModels.LoginUserViewModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.UserEntityUniqueConstraintViolationException;

@Service
public interface UserService {
    UserViewModel register(RegisterUserBindingModel userBindingModel)
            throws UserEntityUniqueConstraintViolationException,
            FieldConstraintViolationException;
    LoginUserViewModel login(LoginBindingModel loginBindingModel) throws EntityNotFoundException;
    UserViewModel getUser(Long id) throws EntityNotFoundException;
    UserViewModel setUserRole(Long userId, String roleName) throws EntityNotFoundException;
    UserEntity getUserEntity(Long id) throws EntityNotFoundException;
    UserEntity getUserEntity(String email) throws EntityNotFoundException;
}
