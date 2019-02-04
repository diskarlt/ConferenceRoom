package com.kakaopay.recruite.conferenceroom.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RoomDto {
    private Long id;
    private final String roomName;
}
