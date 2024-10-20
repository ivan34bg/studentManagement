package org.studentmanagement.data.viewModels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassViewModel {
    private Long id;
    private String title;
    private String description;
    private UserViewModel teacher;
    private UserViewModel[] students;
}
