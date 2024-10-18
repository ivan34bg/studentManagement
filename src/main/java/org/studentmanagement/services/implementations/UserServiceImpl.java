package org.studentmanagement.services.implementations;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.UserNotFoundException;
import org.studentmanagement.services.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserViewModel registerUser(RegisterUserBindingModel userBindingModel) {
        UserEntity userEntity = modelMapper.map(userBindingModel, UserEntity.class);
        userEntity.setRole(RoleEnum.PENDING);

        UserEntity savedUser = userRepository.save(userEntity);

        return modelMapper.map(savedUser, UserViewModel.class);
    }

    @Override
    public UserViewModel getUserByID(Long id) throws UserNotFoundException {
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserViewModel.class);
        }

        throw new UserNotFoundException();
    }
}
