package com.kakaopay.recruite.conferenceroom.dto;

import com.kakaopay.recruite.conferenceroom.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private final String roomName;

    public RoomDto() {
        this.roomName = "";
    }

    public RoomDto(@NotNull Room room) {
        this.id = room.getId();
        this.roomName = room.getRoomName();
    }
}
