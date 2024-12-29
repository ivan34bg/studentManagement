package org.studentmanagement.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.viewModels.ClassViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.RoleRequirementViolationException;
import org.studentmanagement.services.ClassService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/class")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PostMapping()
    public ResponseEntity<ClassViewModel> addClass(@RequestBody AddClassBindingModel classBindingModel)
            throws FieldConstraintViolationException {
        ClassViewModel classViewModel = classService.addClass(classBindingModel);
        return new ResponseEntity<>(classViewModel, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ClassViewModel>> getUserClasses(Principal principal) throws EntityNotFoundException {
        List<ClassViewModel> userClasses = classService.getUserClasses(principal);
        return new ResponseEntity<>(userClasses, HttpStatus.OK);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<ClassViewModel> getClass(@PathVariable Long classId) throws EntityNotFoundException {
        ClassViewModel classViewModel = classService.getClass(classId);
        return new ResponseEntity<>(classViewModel, HttpStatus.OK);
    }

    @PatchMapping("/{classId}/teacher/{teacherId}")
    public ResponseEntity<ClassViewModel> setTeacherToClass(@PathVariable Long classId, @PathVariable Long teacherId)
            throws EntityNotFoundException, RoleRequirementViolationException {
        ClassViewModel classViewModel = classService.setTeacher(classId, teacherId);
        return new ResponseEntity<>(classViewModel, HttpStatus.OK);
    }

    @PostMapping("/{classId}/student/{studentId}")
    public ResponseEntity<ClassViewModel> addStudentToClass(@PathVariable Long classId, @PathVariable Long studentId)
            throws EntityNotFoundException, RoleRequirementViolationException {
        ClassViewModel classViewModel = classService.addStudent(classId, studentId);
        return new ResponseEntity<>(classViewModel, HttpStatus.OK);
    }
}
