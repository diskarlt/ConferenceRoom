package com.kakaopay.recruite.conferenceroom.dto;

import com.kakaopay.recruite.conferenceroom.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private final String userName;

    public UserDto() {
        this.userName = "";
    }

    public UserDto(@NotNull User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
    }
}
