package com.global_class_booking_system.service;

import com.global_class_booking_system.dto.request.AddSessionsRequest;
import com.global_class_booking_system.dto.request.SessionRequest;
import com.global_class_booking_system.dto.response.ApiResponse;
import com.global_class_booking_system.entity.ClassSession;
import com.global_class_booking_system.entity.Offering;
import com.global_class_booking_system.entity.User;
import com.global_class_booking_system.entity.enums.Role;
import com.global_class_booking_system.exception.BadRequestException;
import com.global_class_booking_system.exception.ResourceNotFoundException;
import com.global_class_booking_system.repository.ClassSessionRepository;
import com.global_class_booking_system.repository.OfferingRepository;
import com.global_class_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final OfferingRepository offeringRepository;
    private final UserRepository userRepository;
    private final ClassSessionRepository classSessionRepository;

    public ApiResponse addSessions(Long offeringId, AddSessionsRequest request) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("User is not a teacher");
        }

        if (!offering.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("This teacher does not own this offering");
        }

        ZoneId teacherZone = ZoneId.of(offering.getTeacherTimezone());

        for (SessionRequest sessionRequest : request.getSessions()) {
            if (!sessionRequest.getEndTime().isAfter(sessionRequest.getStartTime())) {
                throw new BadRequestException("Session end time must be after start time");
            }

            Instant startUtc = sessionRequest.getStartTime().atZone(teacherZone).toInstant();
            Instant endUtc = sessionRequest.getEndTime().atZone(teacherZone).toInstant();

            ClassSession session = ClassSession.builder()
                    .offering(offering)
                    .teacher(teacher)
                    .startTimeUtc(startUtc)
                    .endTimeUtc(endUtc)
                    .build();

            classSessionRepository.save(session);
        }

        return new ApiResponse("Sessions added successfully");
    }
}
