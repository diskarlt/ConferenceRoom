package com.kakaopay.recruite.conferenceroom.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.format.DateTimeFormatter;

@Data
@RequiredArgsConstructor
@ScriptAssert(lang="javascript", script = "_.startTime < _.endTime", alias = "_")
public class Reservation {
    private Long id;

    @NotEmpty
    private final String roomName;
    @NotEmpty
    private final String userName;
    @NotEmpty
    private final String subject;
    private final int repeat;

    @NotEmpty
    @Pattern(regexp = "[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]", message = "yyyy-mm-dd 형식이어야 합니다.")
    private final String date;
    @NotEmpty
    @Pattern(regexp = "[0-2][0-9]:[03][0]", message = "예약 시간은 hh:mm 형식의 30분 단위여야 합니다.")
    private final String startTime;
    @NotEmpty
    @Pattern(regexp = "[0-2][0-9]:[03][0]", message = "예약 시간은 hh:mm 형식의 30분 단위여야 합니다.")
    private final String endTime;

    public Reservation(ReservationData data) {
        this.id = data.getId();
        this.roomName = data.getRoomName();
        this.userName = data.getUserName();
        this.subject = data.getSubject();
        this.repeat = data.getRepeat();
        this.date = data.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.startTime = data.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm"));
        this.endTime = data.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm"));
    }
}
