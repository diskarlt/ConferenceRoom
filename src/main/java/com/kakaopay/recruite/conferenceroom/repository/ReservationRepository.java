package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r " +
            " where r.startDate <= :date and " +
            "       r.endDate >= :date and " +
            "       r.dayOfWeek = :dayOfWeek")
    List<Reservation> findAllByDateAndDayOfWeek(@Param("date") LocalDate date, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}