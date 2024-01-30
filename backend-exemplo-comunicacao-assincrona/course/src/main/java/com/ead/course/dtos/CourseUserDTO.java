package com.ead.course.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseUserDTO {

    private UUID courseId;
    private UUID userId;
}
