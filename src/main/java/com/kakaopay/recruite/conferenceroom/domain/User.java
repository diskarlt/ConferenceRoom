package com.kakaopay.recruite.conferenceroom.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Builder
@Entity
@Data
public class User {
    @Id
    @Column(name = "USER_ID")
    private Long id;
    private String userName;
}
