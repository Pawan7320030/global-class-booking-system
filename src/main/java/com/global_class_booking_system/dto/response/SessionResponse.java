package com.global_class_booking_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SessionResponse {
    private Long sessionId;
    private String startTime;
    private String endTime;
}
