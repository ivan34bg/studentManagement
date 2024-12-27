package org.studentmanagement.data.viewModels;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LoginUserViewModel extends UserViewModel {
    private String token;
    private Date expirationDate;
}
