package org.studentmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.studentmanagement.data.bindingModels.RegisterUserBindingModel;
import org.studentmanagement.data.viewModels.UserViewModel;
import org.studentmanagement.exceptions.FieldConstraintViolationException;
import org.studentmanagement.exceptions.UserEntityUniqueConstraintViolationException;
import org.studentmanagement.services.UserService;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<UserViewModel> registerUser(@ModelAttribute RegisterUserBindingModel userBindingModel)
            throws UserEntityUniqueConstraintViolationException,
            FieldConstraintViolationException {
        UserViewModel userViewModel = userService.registerUser(userBindingModel);
        return new ResponseEntity<>(userViewModel, HttpStatus.CREATED);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<UserViewModel> getUser(@PathVariable Long userID) throws NoSuchElementException {
        UserViewModel userViewModel = userService.getUserByID(userID);
        return new ResponseEntity<>(userViewModel, HttpStatus.OK);
    }
}
