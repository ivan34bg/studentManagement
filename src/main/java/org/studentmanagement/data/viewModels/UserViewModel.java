package org.studentmanagement.data.viewModels;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.studentmanagement.data.enums.RoleEnum;

@Getter
@Setter
@EqualsAndHashCode
public class UserViewModel {
    private Long id;
    private String firstName;
    private String lastName;
    private RoleEnum role;
}
