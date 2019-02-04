package com.kakaopay.recruite.conferenceroom.dao;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Builder
@Entity
@Data
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"userName"})
})
public class UserDao {
    @Id @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;
    private String userName;
}
