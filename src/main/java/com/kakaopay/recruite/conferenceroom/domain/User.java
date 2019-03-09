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
public class User {
    @Id
    @Column(name = "USER_ID")
    private Long id;
    private String userName;
}
