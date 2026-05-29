package com.global_class_booking_system.controller;

import com.global_class_booking_system.dto.request.BookOfferingRequest;
import com.global_class_booking_system.dto.response.BookingDetailsResponse;
import com.global_class_booking_system.dto.response.BookingResponse;
import com.global_class_booking_system.dto.response.OfferingResponse;
import com.global_class_booking_system.service.BookingService;
import com.global_class_booking_system.service.OfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class ParentController {

    private final OfferingService offeringService;
    private final BookingService bookingService;

    @GetMapping("/{parentId}/offerings")
    public List<OfferingResponse> getAvailableOfferings(@PathVariable Long parentId) {
        return offeringService.getAvailableOfferingsForParent(parentId);
    }

    @PostMapping("/bookings")
    public BookingResponse bookOffering(@Valid @RequestBody BookOfferingRequest request) {
        return bookingService.bookOffering(request);
    }

    @GetMapping("/{parentId}/bookings")
    public List<BookingDetailsResponse> getParentBookings(@PathVariable Long parentId) {
        return bookingService.getParentBookings(parentId);
    }
}
