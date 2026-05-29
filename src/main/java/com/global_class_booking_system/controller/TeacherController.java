package com.global_class_booking_system.controller;

import com.global_class_booking_system.dto.request.AddSessionsRequest;
import com.global_class_booking_system.dto.request.CreateOfferingRequest;
import com.global_class_booking_system.dto.response.ApiResponse;
import com.global_class_booking_system.dto.response.OfferingCreatedResponse;
import com.global_class_booking_system.dto.response.OfferingResponse;
import com.global_class_booking_system.service.OfferingService;
import com.global_class_booking_system.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final OfferingService offeringService;
    private final SessionService sessionService;

    @PostMapping("/offerings")
    public OfferingCreatedResponse createOffering(@Valid @RequestBody CreateOfferingRequest request) {
        return offeringService.createOffering(request);
    }

    @PostMapping("/offerings/{offeringId}/sessions")
    public ApiResponse addSessions(@PathVariable Long offeringId,
                                   @Valid @RequestBody AddSessionsRequest request) {
        return sessionService.addSessions(offeringId, request);
    }

    @GetMapping("/{teacherId}/offerings")
    public List<OfferingResponse> getTeacherOfferings(@PathVariable Long teacherId) {
        return offeringService.getTeacherOfferings(teacherId);
    }
}
