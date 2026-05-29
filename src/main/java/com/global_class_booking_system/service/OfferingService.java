package com.global_class_booking_system.service;

import com.global_class_booking_system.dto.request.CreateOfferingRequest;
import com.global_class_booking_system.dto.response.OfferingCreatedResponse;
import com.global_class_booking_system.dto.response.OfferingResponse;
import com.global_class_booking_system.dto.response.SessionResponse;
import com.global_class_booking_system.entity.Course;
import com.global_class_booking_system.entity.Offering;
import com.global_class_booking_system.entity.User;
import com.global_class_booking_system.entity.enums.Role;
import com.global_class_booking_system.exception.BadRequestException;
import com.global_class_booking_system.exception.ResourceNotFoundException;
import com.global_class_booking_system.repository.CourseRepository;
import com.global_class_booking_system.repository.OfferingRepository;
import com.global_class_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public OfferingCreatedResponse createOffering(CreateOfferingRequest request) {
        validateTimezone(request.getTeacherTimezone());

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("User is not a teacher");
        }

        if (!teacher.getTimezone().equals(request.getTeacherTimezone())) {
            throw new BadRequestException("Teacher timezone does not match teacher profile timezone");
        }

        Offering offering = Offering.builder()
                .course(course)
                .teacher(teacher)
                .title(request.getTitle())
                .teacherTimezone(request.getTeacherTimezone())
                .capacity(request.getCapacity() == null ? 100 : request.getCapacity())
                .bookedCount(0)
                .build();

        Offering savedOffering = offeringRepository.save(offering);
        return new OfferingCreatedResponse(savedOffering.getId(), "Offering created successfully");
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getTeacherOfferings(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("User is not a teacher");
        }

        return offeringRepository.findByTeacherId(teacherId)
                .stream()
                .map(offering -> mapOfferingToResponse(offering, ZoneId.of(offering.getTeacherTimezone())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getAvailableOfferingsForParent(Long parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        if (parent.getRole() != Role.PARENT) {
            throw new BadRequestException("User is not a parent");
        }

        validateTimezone(parent.getTimezone());
        ZoneId parentZone = ZoneId.of(parent.getTimezone());

        return offeringRepository.findAll()
                .stream()
                .map(offering -> mapOfferingToResponse(offering, parentZone))
                .toList();
    }

    private OfferingResponse mapOfferingToResponse(Offering offering, ZoneId displayZone) {
        List<SessionResponse> sessionResponses = offering.getSessions()
                .stream()
                .map(session -> SessionResponse.builder()
                        .sessionId(session.getId())
                        .startTime(session.getStartTimeUtc().atZone(displayZone).toString())
                        .endTime(session.getEndTimeUtc().atZone(displayZone).toString())
                        .build())
                .toList();

        return OfferingResponse.builder()
                .offeringId(offering.getId())
                .courseTitle(offering.getCourse().getTitle())
                .offeringTitle(offering.getTitle())
                .teacherName(offering.getTeacher().getName())
                .timezone(displayZone.toString())
                .capacity(offering.getCapacity())
                .bookedCount(offering.getBookedCount())
                .sessions(sessionResponses)
                .build();
    }

    private void validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
        } catch (DateTimeException ex) {
            throw new BadRequestException("Invalid timezone: " + timezone);
        }
    }
}
