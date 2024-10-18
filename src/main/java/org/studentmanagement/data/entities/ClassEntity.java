package org.studentmanagement.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.studentmanagement.data.enums.RoleEnum;

import java.util.Arrays;

@Table(name = "classes")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassEntity extends BaseEntity {
    @Column(unique = true)
    @NotBlank
    private String title;
    private String description;
    @ManyToOne
    private UserEntity teacher;
    @ManyToOne
    private UserEntity[] students;

    public boolean addStudent(UserEntity student) {
        if (student.getRole().equals(RoleEnum.STUDENT)) {
            students[students.length - 1] = student;
            return true;
        }

        return false;
    }

    public boolean setStudents(UserEntity[] students) {
        if (Arrays.stream(students).allMatch((user) -> user.getRole().equals(RoleEnum.STUDENT))) {
            this.students = students;
            return true;
        }

        return false;
    }
}
