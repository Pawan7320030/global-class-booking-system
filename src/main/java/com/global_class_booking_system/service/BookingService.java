package com.global_class_booking_system.service;

import com.global_class_booking_system.dto.request.BookOfferingRequest;
import com.global_class_booking_system.dto.response.BookingDetailsResponse;
import com.global_class_booking_system.dto.response.BookingResponse;
import com.global_class_booking_system.dto.response.SessionResponse;
import com.global_class_booking_system.entity.Booking;
import com.global_class_booking_system.entity.Offering;
import com.global_class_booking_system.entity.User;
import com.global_class_booking_system.entity.enums.BookingStatus;
import com.global_class_booking_system.entity.enums.Role;
import com.global_class_booking_system.exception.BadRequestException;
import com.global_class_booking_system.exception.ResourceNotFoundException;
import com.global_class_booking_system.exception.TimeConflictException;
import com.global_class_booking_system.repository.BookingRepository;
import com.global_class_booking_system.repository.OfferingRepository;
import com.global_class_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final OfferingRepository offeringRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingResponse bookOffering(BookOfferingRequest request) {
        User parent = userRepository.findByIdForUpdate(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        if (parent.getRole() != Role.PARENT) {
            throw new BadRequestException("User is not a parent");
        }

        Offering offering = offeringRepository.findByIdForUpdate(request.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (offering.getSessions() == null || offering.getSessions().isEmpty()) {
            throw new BadRequestException("Offering has no sessions");
        }

        boolean alreadyBooked = bookingRepository.existsByParentIdAndOfferingIdAndStatus(
                parent.getId(), offering.getId(), BookingStatus.CONFIRMED
        );

        if (alreadyBooked) {
            throw new BadRequestException("Parent already booked this offering");
        }

        long conflictCount = bookingRepository.countOverlappingBookings(parent.getId(), offering.getId());

        if (conflictCount > 0) {
            throw new TimeConflictException("You already have another booked offering that overlaps with this offering");
        }

        if (offering.getBookedCount() >= offering.getCapacity()) {
            throw new BadRequestException("Offering is full");
        }

        offering.setBookedCount(offering.getBookedCount() + 1);

        Booking booking = Booking.builder()
                .parent(parent)
                .offering(offering)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return new BookingResponse(savedBooking.getId(), "Offering booked successfully");
    }

    @Transactional(readOnly = true)
    public List<BookingDetailsResponse> getParentBookings(Long parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        if (parent.getRole() != Role.PARENT) {
            throw new BadRequestException("User is not a parent");
        }

        ZoneId parentZone = ZoneId.of(parent.getTimezone());

        return bookingRepository.findByParentIdAndStatus(parentId, BookingStatus.CONFIRMED)
                .stream()
                .map(booking -> mapBookingToResponse(booking, parentZone))
                .toList();
    }

    private BookingDetailsResponse mapBookingToResponse(Booking booking, ZoneId parentZone) {
        Offering offering = booking.getOffering();

        List<SessionResponse> sessionResponses = offering.getSessions()
                .stream()
                .map(session -> SessionResponse.builder()
                        .sessionId(session.getId())
                        .startTime(session.getStartTimeUtc().atZone(parentZone).toString())
                        .endTime(session.getEndTimeUtc().atZone(parentZone).toString())
                        .build())
                .toList();

        return BookingDetailsResponse.builder()
                .bookingId(booking.getId())
                .offeringId(offering.getId())
                .courseTitle(offering.getCourse().getTitle())
                .offeringTitle(offering.getTitle())
                .timezone(parentZone.toString())
                .sessions(sessionResponses)
                .build();
    }
}
