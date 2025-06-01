package org.studentmanagement.integration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.studentmanagement.data.bindingModels.AddClassBindingModel;
import org.studentmanagement.data.bindingModels.AddStudentBindingModel;
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

    @PatchMapping("/{classId}")
    public ResponseEntity<ClassViewModel> setTeacherToClass(@PathVariable Long classId,
                                                            @RequestBody AddClassBindingModel classBindingModel)
            throws EntityNotFoundException, RoleRequirementViolationException {
        ClassViewModel classViewModel = classService.updateClass(classId, classBindingModel);
        return new ResponseEntity<>(classViewModel, HttpStatus.OK);
    }

    @PostMapping("/{classId}")
    public ResponseEntity<ClassViewModel> addStudentToClass(@PathVariable Long classId,
                                                            @RequestBody AddStudentBindingModel studentModel)
            throws EntityNotFoundException, RoleRequirementViolationException {
        ClassViewModel classViewModel = classService.addStudent(classId, studentModel);
        return new ResponseEntity<>(classViewModel, HttpStatus.OK);
    }
}
