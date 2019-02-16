package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Long> {
    boolean existsByRoom_IdAndDateAndTime(Long id, LocalDate date, LocalTime time);
    void deleteByReservation_Id(Long id);
}
