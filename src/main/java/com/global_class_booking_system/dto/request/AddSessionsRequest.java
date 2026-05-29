package com.global_class_booking_system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddSessionsRequest {

    @NotNull(message = "Teacher id is required")
    private Long teacherId;

    @Valid
    @NotEmpty(message = "At least one session is required")
    private List<SessionRequest> sessions;
}
