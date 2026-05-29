package com.global_class_booking_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookOfferingRequest {

    @NotNull(message = "Parent id is required")
    private Long parentId;

    @NotNull(message = "Offering id is required")
    private Long offeringId;
}
