package org.studentmanagement.data.bindingModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserBindingModel {
    private String email;
    private String firstName;
    private String lastName;
}
