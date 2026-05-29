package com.global_class_booking_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BookingDetailsResponse {
    private Long bookingId;
    private Long offeringId;
    private String courseTitle;
    private String offeringTitle;
    private String timezone;
    private List<SessionResponse> sessions;
}
