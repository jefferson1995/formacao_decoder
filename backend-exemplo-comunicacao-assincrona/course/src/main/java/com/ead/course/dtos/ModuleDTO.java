package com.ead.course.dtos;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Data
public class ModuleDTO {

    @NotBlank
    private String title;
    @NotBlank
    private String description;


}
