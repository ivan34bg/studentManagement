package org.studentmanagement.services.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import org.studentmanagement.services.ClassService;
import org.studentmanagement.services.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;


    private final UserService userService;

    @Autowired
    public ClassServiceImpl(ClassRepository classRepository, ModelMapper modelMapper, UserService userService) {
        this.classRepository = classRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Override
    public ClassViewModel addClass(AddClassBindingModel classBindingModel) throws FieldConstraintViolationException {
        ClassEntity classEntity = modelMapper.map(classBindingModel, ClassEntity.class);
        Set<ConstraintViolation<ClassEntity>> violations = validator.validate(classEntity);

        if (violations.isEmpty()) {
            ClassEntity savedClass = classRepository.save(classEntity);
            return modelMapper.map(savedClass, ClassViewModel.class);
        } else {
            throw new FieldConstraintViolationException(getViolationMessages(violations));
        }
    }

    @Override
    public List<ClassViewModel> getUserClasses(Principal principal) throws EntityNotFoundException {
        UserEntity user = userService.getUserEntity(principal.getName());
        List<ClassEntity> classes;

        switch (user.getRole()) {
            case TEACHER -> classes = classRepository.findAllByTeacher(user);
            case STUDENT -> classes = classRepository.findAllByStudentsContains(user);
            default -> classes = new ArrayList<>();
        }

        return classes.stream().map(classEntity -> modelMapper.map(classEntity, ClassViewModel.class)).toList();
    }

    private String[] getViolationMessages(Set<ConstraintViolation<ClassEntity>> violations) {
        return violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);
    }

    @Override
    public ClassViewModel getClass(Long id) throws EntityNotFoundException {
        ClassEntity classEntity = getClassEntity(id);
        return modelMapper.map(classEntity, ClassViewModel.class);
    }

    @Override
    public ClassViewModel updateClass(Long classId, AddClassBindingModel classBindingModel)
            throws EntityNotFoundException, RoleRequirementViolationException {
        ClassEntity classEntity = getClassEntity(classId);

        if (classBindingModel.getTeacherId() != null) {
            setTeacher(classEntity, classBindingModel.getTeacherId());
        }

        if (classBindingModel.getStudentIds() != null) {
            clearStudentsOfClass(classEntity);

            for (Long studentId : classBindingModel.getStudentIds()) {
                addStudent(classEntity, studentId);
            }
        }

        return modelMapper.map(classEntity, ClassViewModel.class);
    }

    private void clearStudentsOfClass(ClassEntity classEntity) throws EntityNotFoundException {
        classEntity.setStudents(new ArrayList<>());

        classRepository.save(classEntity);
    }

    private void setTeacher(ClassEntity classEntity, Long teacherId)
            throws EntityNotFoundException, RoleRequirementViolationException {
        UserEntity user = userService.getUserEntity(teacherId);

        if (user.getRole().equals(RoleEnum.TEACHER)) {
            classEntity.setTeacher(user);
            classRepository.save(classEntity);
        } else {
            throw new RoleRequirementViolationException("User is not a teacher");
        }
    }
    @Override
    public ClassViewModel addStudent(Long classId, AddStudentBindingModel studentBindingModel)
            throws EntityNotFoundException, RoleRequirementViolationException {
        ClassEntity classEntity = getClassEntity(classId);
        addStudent(classEntity, studentBindingModel.getStudentId());
        return modelMapper.map(classEntity, ClassViewModel.class);
    }

    private void addStudent(ClassEntity classEntity, Long studentId)
            throws RoleRequirementViolationException, EntityNotFoundException {
        UserEntity user = userService.getUserEntity(studentId);

        if (user.getRole().equals(RoleEnum.STUDENT)) {
            classEntity.addStudent(user);
            classRepository.save(classEntity);
        } else {
            throw new RoleRequirementViolationException(getUserIsNotAStudentErrorText(studentId));
        }
    }

    private String getUserIsNotAStudentErrorText(Long userId) {
        return String.format("User with id %s is not a student", userId);
    }

    private ClassEntity getClassEntity(Long id) throws EntityNotFoundException {
        return classRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
