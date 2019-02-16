package com.kakaopay.recruite.conferenceroom.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Builder
@Entity
@Data
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"roomName"})
})
public class Room {
    @Id @GeneratedValue
    @Column(name = "ROOM_ID")
    private Long id;
    private String roomName;
}
