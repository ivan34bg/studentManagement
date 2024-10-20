package org.studentmanagement.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;
import org.studentmanagement.data.enums.RoleEnum;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    @NotBlank(message = "Username should not be empty")
    @Length(min = 3, message = "Username should have at least 3 symbols")
    @Column(unique = true)
    private String username;
    @NotBlank(message = "First name should not be empty")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    private String lastName;
    @NonNull
    @Enumerated
    private RoleEnum role;
    @ManyToOne
    private ClassEntity[] classes;
}
