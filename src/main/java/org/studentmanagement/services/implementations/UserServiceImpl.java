package org.studentmanagement.services.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.UserEntityUniqueConstraintViolationException;
import org.studentmanagement.services.UserService;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Override
    public UserViewModel registerUser(RegisterUserBindingModel userBindingModel)
            throws UserEntityUniqueConstraintViolationException,
            FieldConstraintViolationException {
        UserEntity userEntity = modelMapper.map(userBindingModel, UserEntity.class);
        userEntity.setRole(RoleEnum.PENDING);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(userEntity);

        if (violations.isEmpty()) {
            return saveUserEntity(userEntity);
        } else {
            throw new FieldConstraintViolationException(getViolationsMessages(violations));
        }
    }

    private UserViewModel saveUserEntity(UserEntity userEntity) throws UserEntityUniqueConstraintViolationException {
        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new UserEntityUniqueConstraintViolationException();
        } else {
            UserEntity savedUser = userRepository.save(userEntity);
            return modelMapper.map(savedUser, UserViewModel.class);
        }
    }

    private <E> String[] getViolationsMessages(Set<ConstraintViolation<E>> violations) {
        return violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);
    }

    @Override
    public UserViewModel getUserByID(Long id) throws NoSuchElementException {
        UserEntity user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return modelMapper.map(user, UserViewModel.class);
    }
}
