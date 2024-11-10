package org.studentmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.studentmanagement.data.bindingModels.LoginBindingModel;
import org.studentmanagement.exceptions.EntityNotFoundException;
import org.studentmanagement.services.UserService;

@Controller
public class LoginController {
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@ModelAttribute LoginBindingModel loginBindingModel)
            throws EntityNotFoundException {
        String token = userService.login(loginBindingModel);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
