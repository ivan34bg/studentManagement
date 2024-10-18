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
    @NotBlank
    @Length(min = 3)
    @Column(unique = true)
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NonNull
    @Enumerated
    private RoleEnum role;
    @ManyToOne
    private ClassEntity[] classes;
}
