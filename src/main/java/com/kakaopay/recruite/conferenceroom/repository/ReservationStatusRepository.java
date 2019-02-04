package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.dao.ReservationStatusDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservationStatusRepository extends JpaRepository<ReservationStatusDao, Long> {
    boolean existsByRoom_IdAndDateAndTime(Long id, LocalDate date, LocalTime time);
    void deleteByReservation_Id(Long id);
}
