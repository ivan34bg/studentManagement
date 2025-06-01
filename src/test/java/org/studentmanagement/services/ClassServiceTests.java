package org.studentmanagement.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.bindingModels.AddStudentBindingModel;
import org.studentmanagement.data.entities.ClassEntity;
import org.studentmanagement.data.entities.UserEntity;
import org.studentmanagement.data.enums.RoleEnum;
import org.studentmanagement.data.repositories.ClassRepository;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.RoleRequirementViolationException;
import org.studentmanagement.services.implementations.ClassServiceImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ClassServiceTests {
    @Mock
    private ClassRepository classRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserService userService;
    private ClassService classService;
    @Autowired
    private ClassServiceImpl classServiceImpl;

    @BeforeEach
    void setUpBeforeTest() {
        classRepository = Mockito.mock(ClassRepository.class);
        modelMapper = Mockito.mock(ModelMapper.class);
        userService = Mockito.mock(UserService.class);
        classService = new ClassServiceImpl(classRepository, modelMapper, userService);
    }

    @Test
    void testAddClassWithoutViolations() throws FieldConstraintViolationException {
        AddClassBindingModel bindingModel = AddClassBindingModel
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build();
        ClassEntity classEntity = ClassEntity
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build();
        ClassViewModel viewModel = ClassViewModel
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build();

        Mockito.when(modelMapper.map(bindingModel, ClassEntity.class)).thenReturn(classEntity);
        Mockito.when(classRepository.save(classEntity)).thenReturn(classEntity);
        Mockito.when(modelMapper.map(classEntity, ClassViewModel.class)).thenReturn(viewModel);

        ClassViewModel result = classService.addClass(bindingModel);

        Assertions.assertEquals(viewModel, result);

        Mockito.verify(modelMapper, Mockito.times(1)).map(bindingModel, ClassEntity.class);
        Mockito.verify(classRepository, Mockito.times(1)).save(classEntity);
        Mockito.verify(modelMapper, Mockito.times(1)).map(classEntity, ClassViewModel.class);
    }

    @Test
    void testAddClassWithViolations() {
        AddClassBindingModel bindingModel = Mockito.mock(AddClassBindingModel.class);
        ClassEntity classEntity = new ClassEntity();

        Mockito.when(modelMapper.map(bindingModel, ClassEntity.class)).thenReturn(classEntity);
        Mockito.when(classRepository.save(classEntity)).thenReturn(classEntity);

        Assertions.assertThrows(FieldConstraintViolationException.class, () -> classService.addClass(bindingModel));
        Mockito.verify(modelMapper, Mockito.times(1)).map(bindingModel, ClassEntity.class);
        Mockito.verify(classRepository, Mockito.times(0)).save(classEntity);
        Mockito.verify(modelMapper, Mockito.times(0)).map(classEntity, ClassViewModel.class);
    }

    @Test
    void testGetUserClassForStudent() throws EntityNotFoundException {
        Principal principal = Mockito.mock(Principal.class);
        String expectedEmail = "test@test.com";
        UserEntity user = Mockito.mock(UserEntity.class);
        List<ClassEntity> userClasses = List.of(ClassEntity
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build());
        ClassViewModel viewModel = ClassViewModel
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build();

        Mockito.when(principal.getName()).thenReturn(expectedEmail);
        Mockito.when(userService.getUserEntity(expectedEmail)).thenReturn(user);
        Mockito.when(user.getRole()).thenReturn(RoleEnum.STUDENT);
        Mockito.when(classRepository.findAllByStudentsContains(user)).thenReturn(userClasses);
        Mockito.when(modelMapper.map(userClasses.getFirst(), ClassViewModel.class)).thenReturn(viewModel);

        List<ClassViewModel> result = classService.getUserClasses(principal);

        Assertions.assertArrayEquals(List.of(viewModel).toArray(), result.toArray());
        Mockito.verify(principal, Mockito.times(1)).getName();
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(expectedEmail);
        Mockito.verify(user, Mockito.times(1)).getRole();
        Mockito.verify(classRepository, Mockito.times(1)).findAllByStudentsContains(user);
        Mockito.verify(classRepository, Mockito.times(0)).findAllByTeacher(user);
        Mockito.verify(modelMapper, Mockito.times(1)).map(userClasses.getFirst(), ClassViewModel.class);
    }

    @Test
    void testGetUserClassForTeacher() throws EntityNotFoundException {
        Principal principal = Mockito.mock(Principal.class);
        String expectedEmail = "test@test.com";
        UserEntity user = Mockito.mock(UserEntity.class);
        List<ClassEntity> userClasses = List.of(ClassEntity
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build());
        ClassViewModel viewModel = ClassViewModel
                .builder()
                .title("ClassTitle")
                .description("ClassDescription")
                .build();

        Mockito.when(principal.getName()).thenReturn(expectedEmail);
        Mockito.when(userService.getUserEntity(expectedEmail)).thenReturn(user);
        Mockito.when(user.getRole()).thenReturn(RoleEnum.TEACHER);
        Mockito.when(classRepository.findAllByTeacher(user)).thenReturn(userClasses);
        Mockito.when(modelMapper.map(userClasses.getFirst(), ClassViewModel.class)).thenReturn(viewModel);

        List<ClassViewModel> result = classService.getUserClasses(principal);

        Assertions.assertArrayEquals(List.of(viewModel).toArray(), result.toArray());
        Mockito.verify(principal, Mockito.times(1)).getName();
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(expectedEmail);
        Mockito.verify(user, Mockito.times(1)).getRole();
        Mockito.verify(classRepository, Mockito.times(0)).findAllByStudentsContains(user);
        Mockito.verify(classRepository, Mockito.times(1)).findAllByTeacher(user);
        Mockito.verify(modelMapper, Mockito.times(1)).map(userClasses.getFirst(), ClassViewModel.class);
    }

    @Test
    void testGetClassWhichExists() throws EntityNotFoundException {
        long expectedId = 1L;
        ClassEntity classEntity = Mockito.mock(ClassEntity.class);
        ClassViewModel viewModel = Mockito.mock(ClassViewModel.class);

        Mockito.when(classRepository.findById(expectedId)).thenReturn(Optional.of(classEntity));
        Mockito.when(modelMapper.map(classEntity, ClassViewModel.class)).thenReturn(viewModel);

        ClassViewModel result = classService.getClass(expectedId);

        Assertions.assertEquals(viewModel, result);
        Mockito.verify(classRepository, Mockito.times(1)).findById(expectedId);
        Mockito.verify(modelMapper, Mockito.times(1)).map(classEntity, ClassViewModel.class);
    }

    @Test
    void testGetClassNonexistentClass() {
        Long expectedId = 1L;

        Mockito.when(classRepository.findById(expectedId)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> classService.getClass(expectedId));
        Mockito.verify(classRepository, Mockito.times(1)).findById(expectedId);
    }

    @Test
    void testUpdateClassWithValidData() throws EntityNotFoundException, RoleRequirementViolationException {
        long expectedClassId = 1L;
        long expectedTeacherId = 2L;
        long expectedStudentId = 3L;
        AddClassBindingModel bindingModel = AddClassBindingModel
                .builder()
                .teacherId(expectedTeacherId)
                .studentIds(new Long[]{expectedStudentId})
                .build();
        ClassEntity classEntity = Mockito.mock(ClassEntity.class);
        UserEntity teacher = Mockito.mock(UserEntity.class);
        UserEntity student = Mockito.mock(UserEntity.class);
        ClassViewModel viewModel = Mockito.mock(ClassViewModel.class);

        Mockito.when(classRepository.findById(expectedClassId)).thenReturn(Optional.of(classEntity));
        Mockito.when(userService.getUserEntity(expectedTeacherId)).thenReturn(teacher);
        Mockito.when(teacher.getRole()).thenReturn(RoleEnum.TEACHER);
        Mockito.when(userService.getUserEntity(expectedStudentId)).thenReturn(student);
        Mockito.when(student.getRole()).thenReturn(RoleEnum.STUDENT);
        Mockito.when(modelMapper.map(classEntity, ClassViewModel.class)).thenReturn(viewModel);

        ClassViewModel result = classService.updateClass(expectedClassId, bindingModel);

        Assertions.assertEquals(viewModel, result);
        Mockito.verify(classRepository, Mockito.times(1)).findById(expectedClassId);
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(expectedTeacherId);
        Mockito.verify(teacher, Mockito.times(1)).getRole();
        Mockito.verify(classEntity, Mockito.times(1)).setTeacher(teacher);
        Mockito.verify(classRepository, Mockito.times(3)).save(classEntity);
        Mockito.verify(classEntity, Mockito.times(1)).setStudents(new ArrayList<>());
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(expectedStudentId);
        Mockito.verify(student, Mockito.times(1)).getRole();
        Mockito.verify(classEntity, Mockito.times(1)).addStudent(student);
        Mockito.verify(modelMapper, Mockito.times(1)).map(classEntity, ClassViewModel.class);
    }

    @Test
    void testUpdateClassWithTeacherIdOnly() throws EntityNotFoundException, RoleRequirementViolationException {
        long expectedClassId = 1L;
        long expectedTeacherId = 2L;
        AddClassBindingModel bindingModel = AddClassBindingModel
                .builder()
                .teacherId(expectedTeacherId)
                .build();
        ClassEntity classEntity = Mockito.mock(ClassEntity.class);
        UserEntity teacher = Mockito.mock(UserEntity.class);
        ClassViewModel viewModel = Mockito.mock(ClassViewModel.class);

        Mockito.when(classRepository.findById(expectedClassId)).thenReturn(Optional.of(classEntity));
        Mockito.when(userService.getUserEntity(expectedTeacherId)).thenReturn(teacher);
        Mockito.when(teacher.getRole()).thenReturn(RoleEnum.TEACHER);
        Mockito.when(modelMapper.map(classEntity, ClassViewModel.class)).thenReturn(viewModel);

        ClassViewModel result = classService.updateClass(expectedClassId, bindingModel);

        Assertions.assertEquals(viewModel, result);
        Mockito.verify(classRepository, Mockito.times(1)).findById(expectedClassId);
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(expectedTeacherId);
        Mockito.verify(teacher, Mockito.times(1)).getRole();
        Mockito.verify(classEntity, Mockito.times(1)).setTeacher(teacher);
        Mockito.verify(classRepository, Mockito.times(1)).save(classEntity);
        Mockito.verify(classEntity, Mockito.times(0)).setStudents(new ArrayList<>());
        Mockito.verify(modelMapper, Mockito.times(1)).map(classEntity, ClassViewModel.class);
    }

    @Test
    void testUpdateClassWithStudentIdsOnly() throws EntityNotFoundException, RoleRequirementViolationException {
        long expectedClassId = 1L;
        long expectedStudentId = 3L;
        AddClassBindingModel bindingModel = AddClassBindingModel
                .builder()
                .studentIds(new Long[]{expectedStudentId})
                .build();
        ClassEntity classEntity = Mockito.mock(ClassEntity.class);
        UserEntity student = Mockito.mock(UserEntity.class);
        ClassViewModel viewModel = Mockito.mock(ClassViewModel.class);

        Mockito.when(classRepository.findById(expectedClassId)).thenReturn(Optional.of(classEntity));
        Mockito.when(userService.getUserEntity(expectedStudentId)).thenReturn(student);
        Mockito.when(student.getRole()).thenReturn(RoleEnum.STUDENT);
        Mockito.when(modelMapper.map(classEntity, ClassViewModel.class)).thenReturn(viewModel);

        ClassViewModel result = classService.updateClass(expectedClassId, bindingModel);

        Assertions.assertEquals(viewModel, result);
        Mockito.verify(classRepository, Mockito.times(1)).findById(expectedClassId);
        Mockito.verify(classRepository, Mockito.times(2)).save(classEntity);
        Mockito.verify(classEntity, Mockito.times(1)).setStudents(new ArrayList<>());
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(expectedStudentId);
        Mockito.verify(student, Mockito.times(1)).getRole();
        Mockito.verify(classEntity, Mockito.times(1)).addStudent(student);
        Mockito.verify(modelMapper, Mockito.times(1)).map(classEntity, ClassViewModel.class);
    }

    @Test
    void testAddStudentValidData() throws EntityNotFoundException, RoleRequirementViolationException {
        long expectedClassId = 1L;
        AddStudentBindingModel bindingModel = Mockito.mock(AddStudentBindingModel.class);
        ClassEntity classEntity = Mockito.mock(ClassEntity.class);
        UserEntity student = Mockito.mock(UserEntity.class);
        ClassViewModel viewModel = Mockito.mock(ClassViewModel.class);

        Mockito.when(classRepository.findById(expectedClassId)).thenReturn(Optional.of(classEntity));
        Mockito.when(userService.getUserEntity(bindingModel.getStudentId())).thenReturn(student);
        Mockito.when(student.getRole()).thenReturn(RoleEnum.STUDENT);
        Mockito.when(modelMapper.map(classEntity, ClassViewModel.class)).thenReturn(viewModel);

        ClassViewModel result = classService.addStudent(expectedClassId, bindingModel);

        Assertions.assertEquals(viewModel, result);
        Mockito.verify(classRepository, Mockito.times(1)).findById(expectedClassId);
        Mockito.verify(userService, Mockito.times(1)).getUserEntity(bindingModel.getStudentId());
        Mockito.verify(student, Mockito.times(1)).getRole();
        Mockito.verify(classEntity, Mockito.times(1)).addStudent(student);
        Mockito.verify(classRepository, Mockito.times(1)).save(classEntity);
        Mockito.verify(modelMapper, Mockito.times(1)).map(classEntity, ClassViewModel.class);
    }
}
