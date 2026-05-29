package com.global_class_booking_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfferingCreatedResponse {
    private Long offeringId;
    private String message;
}
