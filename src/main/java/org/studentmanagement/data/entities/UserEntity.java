package org.studentmanagement.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.studentmanagement.data.enums.RoleEnum;

import java.util.LinkedList;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
public class UserEntity extends BaseEntity {
    @NotBlank(message = "Email should not be empty")
    @Pattern(regexp = "(^[a-zA-Z]+\\w{2,})@([a-z]{3,}).([a-z]{3,})",
            message = "Email is invalid")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Password should not be empty")
    @Size(min = 8, message = "Password cannot be less than 8 symbols")
    private String password;
    @NotBlank(message = "First name should not be empty")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    private String lastName;
    @NonNull
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @ManyToMany
    private List<UserEntity> classes;

    public UserEntity() {
        role = RoleEnum.PENDING;
        classes = new LinkedList<>();
    }
}
