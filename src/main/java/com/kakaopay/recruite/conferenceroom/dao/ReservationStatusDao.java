package com.kakaopay.recruite.conferenceroom.dao;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Entity
@Data
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"room_id", "date", "time"})
})
public class ReservationStatusDao {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private RoomDao room;
    private final LocalDate date;
    private final LocalTime time;

    @ManyToOne
    @JoinColumn(name = "RESERVATION_ID")
    private ReservationDao reservation;
}
