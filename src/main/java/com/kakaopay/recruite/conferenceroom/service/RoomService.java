package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.dao.RoomDao;
import com.kakaopay.recruite.conferenceroom.dto.RoomDto;
import com.kakaopay.recruite.conferenceroom.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    public void createRoom(RoomDto roomDto) {
        RoomDao roomDao = RoomDao.builder().roomName(roomDto.getRoomName()).build();
        roomRepository.save(roomDao);
        roomDto.setId(roomDao.getId());
    }

    public Optional<RoomDao> findRoom(Long id) {
        return roomRepository.findById(id);
    }

    public List<RoomDto> findAllRoom() {
        List<RoomDto> roomDtoList = new ArrayList<>();
        roomRepository.findAll().forEach(room -> roomDtoList.add(RoomDto.builder().id(room.getId()).roomName(room.getRoomName()).build()));
        return roomDtoList;
    }

}
