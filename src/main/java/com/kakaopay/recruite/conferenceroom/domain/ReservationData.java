package com.kakaopay.recruite.conferenceroom.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@RequiredArgsConstructor
@Data
@Table(indexes = {@Index(columnList = "roomName, startDate, endDate, dayOfWeek, startTime, endTime")})
public class ReservationData {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String roomName;
    @NonNull
    private String userName;
    @NonNull
    private int repeat;

    @NonNull
    private DayOfWeek dayOfWeek;
    @NonNull
    private LocalDate startDate;
    @NonNull
    private LocalDate endDate;
    @NonNull
    private LocalTime startTime;
    @NonNull
    private LocalTime endTime;

    public ReservationData() {}
    public ReservationData(Reservation req) {
        LocalDate date = LocalDate.of(
                Integer.valueOf(req.getDate().split("-")[0]),
                Integer.valueOf(req.getDate().split("-")[1]),
                Integer.valueOf(req.getDate().split("-")[2])
        );
        LocalTime startTime = LocalTime.of(
                Integer.valueOf(req.getStartTime().split(":")[0]),
                Integer.valueOf(req.getStartTime().split(":")[1])
        );
        LocalTime endTime = LocalTime.of(
                Integer.valueOf(req.getEndTime().split(":")[0]),
                Integer.valueOf(req.getEndTime().split(":")[1])
        );

        this.roomName = req.getRoomName();
        this.userName = req.getUserName();
        this.repeat = req.getRepeat();
        this.dayOfWeek = date.getDayOfWeek();
        this.startDate = date;
        this.endDate = date.plusWeeks(repeat);
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
