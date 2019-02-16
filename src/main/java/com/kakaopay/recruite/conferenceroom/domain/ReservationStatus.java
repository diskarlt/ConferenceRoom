package com.kakaopay.recruite.conferenceroom.domain;

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
public class ReservationStatus {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    private final LocalDate date;
    private final LocalTime time;

    @ManyToOne
    @JoinColumn(name = "RESERVATION_ID")
    private Reservation reservation;
}
