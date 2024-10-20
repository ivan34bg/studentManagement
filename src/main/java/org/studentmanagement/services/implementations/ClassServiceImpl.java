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
import org.studentmanagement.data.repositories.ClassRepository;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.services.ClassService;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class ClassServiceImpl implements ClassService {
    private final ClassRepository classRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @Autowired
    public ClassServiceImpl(ClassRepository classRepository, ModelMapper modelMapper) {
        this.classRepository = classRepository;
        this.modelMapper = modelMapper;
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
    public ClassViewModel getClassById(Long id) throws NoSuchElementException {
        ClassEntity classEntity =  classRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return modelMapper.map(classEntity, ClassViewModel.class);
    }
}
