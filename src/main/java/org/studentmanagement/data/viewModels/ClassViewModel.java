package org.studentmanagement.data.viewModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassViewModel {
    private Long id;
    private String title;
    private String description;
    private UserViewModel teacher;
    private UserViewModel[] students;
}
