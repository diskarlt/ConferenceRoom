package com.kakaopay.recruite.conferenceroom.dao;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Builder
@Entity
@Data
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"roomName"})
})
public class RoomDao {
    @Id @GeneratedValue
    @Column(name = "ROOM_ID")
    private Long id;
    private String roomName;
}
