package com.kakaopay.recruite.conferenceroom.web;

import com.kakaopay.recruite.conferenceroom.dto.RoomDto;
import com.kakaopay.recruite.conferenceroom.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RoomController {
    @Autowired
    RoomService roomService;

    @PostMapping(value = "/rooms", consumes = "application/json")
    public ResponseEntity<RoomDto> create(@Valid @RequestBody RoomDto room) {
        roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.OK).body(room);
    }

    @GetMapping(value = "/rooms")
    public List<RoomDto> findAll() {
        return roomService.findAllRoom();
    }
}
