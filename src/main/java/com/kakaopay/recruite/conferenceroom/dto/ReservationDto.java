package com.kakaopay.recruite.conferenceroom.dto;

import com.kakaopay.recruite.conferenceroom.dao.ReservationDao;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.format.DateTimeFormatter;

@Data
@RequiredArgsConstructor
@ScriptAssert(lang="javascript", script = "_.startTime < _.endTime", alias = "_")
public class ReservationDto {
    private Long id;

    private final UserDto user;
    private final RoomDto room;
    private final int repeat;

    @NotEmpty(message = "예약일자는 필수입니다.")
    @Pattern(regexp = "[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]", message = "yyyy-MM-dd 형식이어야 합니다.")
    private final String date;
    @NotEmpty(message = "시작시간은 필수입니다.")
    @Pattern(regexp = "[0-2][0-9]:[03][0]", message = "예약 시간은 HH:mm 형식의 30분 단위여야 합니다.")
    private final String startTime;
    @NotEmpty(message = "종료시간은 필수입니다.")
    @Pattern(regexp = "[0-2][0-9]:[03][0]", message = "예약 시간은 HH:mm 형식의 30분 단위여야 합니다.")
    private final String endTime;

    public ReservationDto(ReservationDao data) {
        this.id = data.getId();
        this.user = UserDto.builder().id(data.getUser().getId()).userName(data.getUser().getUserName()).build();
        this.room = RoomDto.builder().id(data.getRoom().getId()).roomName(data.getRoom().getRoomName()).build();
        this.repeat = data.getRepeat();
        this.date = data.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.startTime = data.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.endTime = data.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
