package com.kakaopay.recruite.conferenceroom.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private Long id;
    private final String userName;
}
