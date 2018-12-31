package com.kakaopay.recruite.conferenceroom.web;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import com.kakaopay.recruite.conferenceroom.domain.Reservation;
import com.kakaopay.recruite.conferenceroom.service.ReservationService;
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
    ReservationService reservationService;

    @PostMapping(value = "/reservations", consumes = "application/json")
    public ResponseEntity<Void> create(@Valid @RequestBody Reservation req) {
        log.debug(req.toString());
        ReservationData reservationData = new ReservationData(req);

        if(reservationService.createReservation(reservationData) == false)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/reservations")
    public List<Reservation> find(@RequestParam Map<String, String> parameters) {
        int year = Integer.valueOf(parameters.get("year"));
        int month = Integer.valueOf(parameters.get("month"));
        int day = Integer.valueOf(parameters.get("day"));
        LocalDate date = LocalDate.of(year, month, day);

        List<Reservation> reservationList = new ArrayList<>();

        reservationService.findReservation(date).forEach(reservationData -> reservationList.add(new Reservation(reservationData)));
        return reservationList;
    }

    @DeleteMapping(value = "/reservations/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }
}
