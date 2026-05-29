package com.global_class_booking_system.repository;

import com.global_class_booking_system.entity.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    List<ClassSession> findByOfferingId(Long offeringId);
}
