package com.global_class_booking_system.repository;

import com.global_class_booking_system.entity.Booking;
import com.global_class_booking_system.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByParentIdAndOfferingIdAndStatus(Long parentId, Long offeringId, BookingStatus status);

    List<Booking> findByParentIdAndStatus(Long parentId, BookingStatus status);

    @Query(value = """
            SELECT COUNT(*)
            FROM bookings b
            JOIN class_sessions existing_sessions
                ON existing_sessions.offering_id = b.offering_id
            JOIN class_sessions new_sessions
                ON new_sessions.offering_id = :newOfferingId
            WHERE b.parent_id = :parentId
              AND b.status = 'CONFIRMED'
              AND existing_sessions.start_time_utc < new_sessions.end_time_utc
              AND existing_sessions.end_time_utc > new_sessions.start_time_utc
            """, nativeQuery = true)
    long countOverlappingBookings(@Param("parentId") Long parentId,
                                  @Param("newOfferingId") Long newOfferingId);
}
