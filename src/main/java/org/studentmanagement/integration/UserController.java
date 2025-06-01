package org.studentmanagement.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.UserEntityUniqueConstraintViolationException;
import org.studentmanagement.services.UserService;

@Controller
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserViewModel> registerUser(@RequestBody RegisterUserBindingModel userBindingModel)
            throws UserEntityUniqueConstraintViolationException,
            FieldConstraintViolationException {
        UserViewModel userViewModel = userService.register(userBindingModel);
        return new ResponseEntity<>(userViewModel, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserViewModel> getUser(@PathVariable Long userId) throws EntityNotFoundException {
        UserViewModel userViewModel = userService.getUser(userId);
        return new ResponseEntity<>(userViewModel, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserViewModel> setUserRole(@PathVariable Long userId, @RequestBody String roleName)
            throws EntityNotFoundException {
        UserViewModel userViewModel = userService.setUserRole(userId, roleName);
        return new ResponseEntity<>(userViewModel, HttpStatus.OK);
    }
}
