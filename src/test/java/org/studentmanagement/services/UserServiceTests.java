package org.studentmanagement.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.h2.engine.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.studentmanagement.data.bindingModels.LoginBindingModel;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.entities.TokenEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.UserRepository;
import org.studentmanagement.data.viewModels.LoginUserViewModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.UserEntityUniqueConstraintViolationException;
import org.studentmanagement.services.implementations.UserServiceImpl;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private Validator validator;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private RoleService roleService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        modelMapper = Mockito.mock(ModelMapper.class);
        validator = Mockito.mock(Validator.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtTokenService = Mockito.mock(JwtTokenService.class);
        roleService = Mockito.mock(RoleService.class);

        userService = new UserServiceImpl(
                userRepository,
                modelMapper,
                validator,
                authenticationManager,
                passwordEncoder,
                jwtTokenService,
                roleService
        );
    }

    @Test
    void testRegisterValidUser() throws UserEntityUniqueConstraintViolationException, FieldConstraintViolationException {
        RegisterUserBindingModel bindingModel = Mockito.mock(RegisterUserBindingModel.class);
        UserEntity userEntity = Mockito.mock(UserEntity.class);
        Set<ConstraintViolation<UserEntity>> constraintViolations = Mockito.mock(Set.class);
        String userEmail = "mail";
        String userPassword = "password";
        String encodedPassword = "encodedPassword";
        UserViewModel viewModel = Mockito.mock(UserViewModel.class);

        Mockito.when(modelMapper.map(bindingModel, UserEntity.class)).thenReturn(userEntity);
        Mockito.when(validator.validate(userEntity)).thenReturn(constraintViolations);
        Mockito.when(constraintViolations.isEmpty()).thenReturn(true);
        Mockito.when(userEntity.getEmail()).thenReturn(userEmail);
        Mockito.when(userRepository.existsByEmail(userEmail)).thenReturn(false);
        Mockito.when(userEntity.getPassword()).thenReturn(userPassword);
        Mockito.when(passwordEncoder.encode(userPassword)).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(userEntity)).thenReturn(userEntity);
        Mockito.when(modelMapper.map(userEntity, UserViewModel.class)).thenReturn(viewModel);

        UserViewModel result = userService.register(bindingModel);

        Mockito.verify(modelMapper, Mockito.times(1)).map(bindingModel, UserEntity.class);
        Mockito.verify(validator, Mockito.times(1)).validate(userEntity);
        Mockito.verify(constraintViolations, Mockito.times(1)).isEmpty();
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(userEmail);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(userPassword);
        Mockito.verify(userEntity, Mockito.times(1)).setPassword(encodedPassword);
        Mockito.verify(userRepository, Mockito.times(1)).save(userEntity);
        Mockito.verify(modelMapper, Mockito.times(1)).map(userEntity, UserViewModel.class);

        Assertions.assertEquals(viewModel, result);
    }

    @Test
    void testLoginValidUser() throws EntityNotFoundException {
        LoginBindingModel bindingModel = Mockito.mock(LoginBindingModel.class);
        String userEmail = "mail";
        String userPassword = "password";
        UserEntity userEntity = Mockito.mock(UserEntity.class);
        TokenEntity tokenEntity = Mockito.mock(TokenEntity.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        LoginUserViewModel viewModel = Mockito.mock(LoginUserViewModel.class);
        String token = "token";
        Date expirationDate = Mockito.mock(Date.class);

        Mockito.when(bindingModel.getEmail()).thenReturn(userEmail);
        Mockito.when(bindingModel.getPassword()).thenReturn(userPassword);
        Mockito.when(userRepository.findUserEntityByEmail(userEmail)).thenReturn(Optional.of(userEntity));
        Mockito.when(jwtTokenService.generateToken(userEntity)).thenReturn(tokenEntity);
        Mockito.when(authenticationManager
                        .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(userEmail, userPassword)))
                .thenReturn(authentication);
        Mockito.when(modelMapper.map(userEntity, LoginUserViewModel.class)).thenReturn(viewModel);
        Mockito.when(tokenEntity.getToken()).thenReturn(token);
        Mockito.when(tokenEntity.getExpirationDate()).thenReturn(expirationDate);

        LoginUserViewModel result = userService.login(bindingModel);

        Mockito.verify(bindingModel, Mockito.times(2)).getEmail();
        Mockito.verify(userRepository, Mockito.times(1)).findUserEntityByEmail(userEmail);
        Mockito.verify(jwtTokenService, Mockito.times(1)).generateToken(userEntity);
        Mockito.verify(authenticationManager, Mockito.times(1))
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(userEmail, userPassword));
        Mockito.verify(modelMapper, Mockito.times(1)).map(userEntity, LoginUserViewModel.class);
        Mockito.verify(tokenEntity, Mockito.times(1)).getToken();
        Mockito.verify(viewModel, Mockito.times(1)).setToken(token);
        Mockito.verify(viewModel, Mockito.times(1)).setExpirationDate(expirationDate);
        Assertions.assertEquals(viewModel, result);
    }

    @Test
    void testGetUserViewModelById() throws EntityNotFoundException {
        long userId = 1L;
        UserEntity userEntity = Mockito.mock(UserEntity.class);
        UserViewModel viewModel = Mockito.mock(UserViewModel.class);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(modelMapper.map(userEntity, UserViewModel.class)).thenReturn(viewModel);

        UserViewModel result = userService.getUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(modelMapper, Mockito.times(1)).map(userEntity, UserViewModel.class);
        Assertions.assertEquals(viewModel, result);
    }

    @Test
    void testSetUserRole() throws EntityNotFoundException {
        long userId = 1L;
        String roleName = RoleEnum.STUDENT.name();
        RoleEnum role = RoleEnum.STUDENT;
        UserEntity userEntity = Mockito.mock(UserEntity.class);
        UserViewModel viewModel = Mockito.mock(UserViewModel.class);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(roleService.getRole(roleName)).thenReturn(role);
        Mockito.when(modelMapper.map(userEntity, UserViewModel.class)).thenReturn(viewModel);

        UserViewModel result = userService.setUserRole(userId, roleName);

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(roleService, Mockito.times(1)).getRole(roleName);
        Mockito.verify(userEntity, Mockito.times(1)).setRole(role);
        Mockito.verify(userRepository, Mockito.times(1)).save(userEntity);
        Assertions.assertEquals(viewModel, result);
    }

    @Test
    void testGetUserEntityById() throws EntityNotFoundException {
        long userId = 1L;
        UserEntity userEntity = Mockito.mock(UserEntity.class);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.getUserEntity(userId);

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Assertions.assertEquals(userEntity, result);
    }

    @Test
    void testGetUserEntityByEmail() throws EntityNotFoundException {
        String email = "mail";
        UserEntity userEntity = Mockito.mock(UserEntity.class);

        Mockito.when(userRepository.findUserEntityByEmail(email)).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.getUserEntity(email);

        Mockito.verify(userRepository, Mockito.times(1)).findUserEntityByEmail(email);
        Assertions.assertEquals(userEntity, result);
    }
}
