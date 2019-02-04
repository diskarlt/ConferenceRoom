package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.dao.ReservationDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationDao, Long> {
    @Query("select r from ReservationDao r " +
            " where r.startDate <= :date and " +
            "       r.endDate >= :date and " +
            "       r.dayOfWeek = :dayOfWeek")
    List<ReservationDao> findAllByDateAndDayOfWeek(@Param("date") LocalDate date, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}