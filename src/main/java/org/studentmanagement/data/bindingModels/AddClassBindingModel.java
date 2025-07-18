package org.studentmanagement.data.bindingModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddClassBindingModel {
    private String title;
    private String description;
    private Long teacherId;
    private Long[] studentIds;
}
