package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.domain.Room;
import com.kakaopay.recruite.conferenceroom.dto.RoomDto;
import com.kakaopay.recruite.conferenceroom.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    public void createRoom(RoomDto roomDto) {
        Room room = Room.builder()
                .roomName(roomDto.getRoomName())
                .build();
        roomRepository.save(room);
        roomDto.setId(room.getId());
    }

    public Optional<Room> findRoom(Long id) {
        return roomRepository.findById(id);
    }

    public List<RoomDto> findAllRoom() {
        return roomRepository.findAll()
                .stream()
                .map(RoomDto::new)
                .collect(toList());
    }

}
