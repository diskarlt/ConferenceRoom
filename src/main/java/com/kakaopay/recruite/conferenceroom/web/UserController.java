package com.kakaopay.recruite.conferenceroom.web;

import com.kakaopay.recruite.conferenceroom.dto.RoomDto;
import com.kakaopay.recruite.conferenceroom.dto.UserDto;
import com.kakaopay.recruite.conferenceroom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/users", consumes = "application/json")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto user) {
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping(value = "/users/{userName}")
    public UserDto find(@PathVariable String userName) {
        return userService.findUserByUserName(userName);
    }
}
