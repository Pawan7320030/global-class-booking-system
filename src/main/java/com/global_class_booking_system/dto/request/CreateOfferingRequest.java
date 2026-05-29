package com.global_class_booking_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOfferingRequest {

    @NotNull(message = "Course id is required")
    private Long courseId;

    @NotNull(message = "Teacher id is required")
    private Long teacherId;

    @NotBlank(message = "Offering title is required")
    private String title;

    @NotBlank(message = "Teacher timezone is required")
    private String teacherTimezone;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
