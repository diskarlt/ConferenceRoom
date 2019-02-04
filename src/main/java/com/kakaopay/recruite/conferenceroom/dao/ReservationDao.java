package com.kakaopay.recruite.conferenceroom.dao;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Entity
@Data
public class ReservationDao {
    @Id @GeneratedValue
    @Column(name = "RESERVATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private final UserDao user;
    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private final RoomDao room;

    private final int repeat;
    private final DayOfWeek dayOfWeek;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
}
