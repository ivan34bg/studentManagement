package org.studentmanagement.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.studentmanagement.data.bindingModels.LoginBindingModel;
import org.studentmanagement.data.viewModels.LoginUserViewModel;
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
    public ResponseEntity<LoginUserViewModel> login(@RequestBody LoginBindingModel loginBindingModel)
            throws EntityNotFoundException {
        LoginUserViewModel model = userService.login(loginBindingModel);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }
}
