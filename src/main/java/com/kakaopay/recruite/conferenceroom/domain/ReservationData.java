package com.kakaopay.recruite.conferenceroom.domain;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
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
    private String subject;
    @NonNull
    private int repeat;

    @NonNull
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private LocalDate startDate;
    private LocalDate endDate;
    @NonNull
    private LocalTime startTime;
    @NonNull
    private LocalTime endTime;

    public ReservationData() {}
    public ReservationData(String roomName, String userName, String subject, int repeat, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.roomName = roomName;
        this.userName = userName;
        this.subject = subject;
        this.repeat = repeat;
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();
        this.startDate = date;
        this.endDate = date.plusWeeks(repeat);
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
