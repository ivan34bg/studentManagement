package org.studentmanagement.services.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
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
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
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
    public ClassViewModel setTeacher(Long classId, Long teacherId)
            throws EntityNotFoundException, RoleRequirementViolationException {
        UserEntity user = userService.getUserEntity(teacherId);

        if (user.getRole().equals(RoleEnum.TEACHER)) {
            ClassEntity classEntity = getClassEntity(classId);
            classEntity.setTeacher(user);
            classRepository.save(classEntity);

            return modelMapper.map(classEntity, ClassViewModel.class);
        }

        throw new RoleRequirementViolationException("User is not a teacher");
    }

    @Override
    public ClassViewModel addStudent(Long classId, Long studentId)
            throws EntityNotFoundException, RoleRequirementViolationException {
        UserEntity user = userService.getUserEntity(studentId);

        if (user.getRole().equals(RoleEnum.STUDENT)) {
            ClassEntity classEntity = getClassEntity(classId);
            classEntity.addStudent(user);
            classRepository.save(classEntity);

            return modelMapper.map(classEntity, ClassViewModel.class);
        }

        throw new RoleRequirementViolationException("User is not a student");
    }

    private ClassEntity getClassEntity(Long id) throws EntityNotFoundException {
        return classRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Class not found"));
    }
}
