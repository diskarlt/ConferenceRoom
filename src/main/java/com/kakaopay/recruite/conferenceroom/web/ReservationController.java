package com.kakaopay.recruite.conferenceroom.web;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import com.kakaopay.recruite.conferenceroom.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    //@RequestMapping(value="/reservations", method = RequestMethod.POST, consumes = "application/json")
    @PostMapping(value = "/reservations", consumes = "application/json")
    public ResponseEntity<Void> create(@RequestBody ReservationData req) {
        log.debug(req.toString());

        if( req.getStartTime().getMinute() != 0 && req.getStartTime().getMinute() != 30 ||
            req.getEndTime().getMinute() != 0 && req.getEndTime().getMinute() != 30)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if(req.getStartTime().compareTo(req.getEndTime()) >= 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if(reservationService.createReservation(req) == false)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/reservations")
    public List<ReservationData> find(@RequestParam Map<String, String> parameters) {
        int year = Integer.valueOf(parameters.get("year"));
        int month = Integer.valueOf(parameters.get("month"));
        int day = Integer.valueOf(parameters.get("day"));
        LocalDate date = LocalDate.of(year, month, day);

        return reservationService.findReservation(date);
    }

    @DeleteMapping(value = "/reservations/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.cancelReservation(id);
    }
}
