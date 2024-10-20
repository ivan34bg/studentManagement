package org.studentmanagement.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.services.ClassService;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/class")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PostMapping()
    public ResponseEntity<ClassViewModel> addClass(@ModelAttribute AddClassBindingModel classBindingModel)
            throws FieldConstraintViolationException {
        ClassViewModel viewModel = classService.addClass(classBindingModel);
        return new ResponseEntity<>(viewModel, HttpStatus.CREATED);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ClassViewModel> getClassById(@PathVariable Long classId)
            throws NoSuchElementException {
        ClassViewModel viewModel = classService.getClassById(classId);
        return new ResponseEntity<>(viewModel, HttpStatus.OK);
    }
}
