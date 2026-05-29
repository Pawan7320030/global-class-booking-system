package com.global_class_booking_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OfferingResponse {
    private Long offeringId;
    private String courseTitle;
    private String offeringTitle;
    private String teacherName;
    private String timezone;
    private Integer capacity;
    private Integer bookedCount;
    private List<SessionResponse> sessions;
}
