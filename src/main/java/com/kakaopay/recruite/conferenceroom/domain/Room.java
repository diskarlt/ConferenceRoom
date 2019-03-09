package com.kakaopay.recruite.conferenceroom.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"roomName"})
})
public class Room {
    @Id @GeneratedValue
    @Column(name = "ROOM_ID")
    private Long id;
    private String roomName;
}
