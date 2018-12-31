package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

public interface ReservationRepository extends CrudRepository<ReservationData, Long> {
    @Query("select case when count(e)>0 then true else false end " +
            "from ReservationData e " +
            "where e.roomName = :roomName and " +
            "      e.startDate <= :endDate and e.endDate >= :startDate and" +
            "      e.dayOfWeek = :dayOfWeek and " +
            "      e.startTime < :endTime and e.endTime > :startTime")
    boolean isConflict(@Param("roomName") String roomName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    @Query("select e from ReservationData e " +
            "where e.startDate <= :date and " +
            "      e.endDate >= :date and " +
            "      e.dayOfWeek = :dayOfWeek")
    List<ReservationData> findAllByDateAndDayOfWeek(@Param("date") LocalDate date, @Param("dayOfWeek") DayOfWeek dayOfWeek);
}