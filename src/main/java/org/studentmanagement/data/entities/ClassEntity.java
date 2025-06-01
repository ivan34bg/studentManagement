package org.studentmanagement.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Table(name = "classes")
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ClassEntity extends BaseEntity {
    @Column(unique = true)
    @NotBlank(message = "Title should not be empty")
    private String title;
    private String description;
    @ManyToOne
    private UserEntity teacher;
    @ManyToMany
    private List<UserEntity> students;

    public ClassEntity() {
        students = new LinkedList<>();
    }

    public void addStudent(UserEntity student) {
        students.add(student);
    }
}
