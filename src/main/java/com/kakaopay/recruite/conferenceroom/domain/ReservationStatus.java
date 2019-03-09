package com.kakaopay.recruite.conferenceroom.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"room_id", "date", "time"})
})
public class ReservationStatus {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    private LocalDate date;
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "RESERVATION_ID")
    private Reservation reservation;
}
