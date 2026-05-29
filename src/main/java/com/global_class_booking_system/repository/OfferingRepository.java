package com.global_class_booking_system.repository;

import com.global_class_booking_system.entity.Offering;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferingRepository extends JpaRepository<Offering, Long> {

    List<Offering> findByTeacherId(Long teacherId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offering o WHERE o.id = :offeringId")
    Optional<Offering> findByIdForUpdate(@Param("offeringId") Long offeringId);
}
