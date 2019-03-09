package com.kakaopay.recruite.conferenceroom.web;

import com.kakaopay.recruite.conferenceroom.domain.Room;
import com.kakaopay.recruite.conferenceroom.dto.ReservationDto;
import com.kakaopay.recruite.conferenceroom.domain.User;
import com.kakaopay.recruite.conferenceroom.service.ReservationService;
import com.kakaopay.recruite.conferenceroom.service.RoomService;
import com.kakaopay.recruite.conferenceroom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;


    @PostMapping(value = "/reservations", consumes = "application/json")
    public ResponseEntity<Void> create(@Valid @RequestBody ReservationDto req) {
        Room room = roomService.findRoom(req.getRoom().getId()).orElse(null);
        if (room == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        User user = userService.findUser(req.getUser().getId()).orElse(null);
        if (user == null) {
            user = userService.createUser(req.getUser());
            log.info(user.toString());
        }

        try {
            reservationService.createReservation(user, room, req);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/reservations")
    public List<ReservationDto> find(@RequestParam Map<String, String> parameters) {
        int year, month, day;
        if(parameters.isEmpty() || parameters.get("year").isEmpty() || parameters.get("month").isEmpty() || parameters.get("day").isEmpty()) {
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
            day = LocalDate.now().getDayOfMonth();
        } else {
            year = Integer.valueOf(parameters.get("year"));
            month = Integer.valueOf(parameters.get("month"));
            day = Integer.valueOf(parameters.get("day"));
        }
        return reservationService.findReservation(LocalDate.of(year, month, day));
    }

    @DeleteMapping(value = "/reservations/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }
}
