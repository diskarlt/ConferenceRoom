package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.dao.RoomDao;
import com.kakaopay.recruite.conferenceroom.dao.UserDao;
import com.kakaopay.recruite.conferenceroom.dto.RoomDto;
import com.kakaopay.recruite.conferenceroom.dto.UserDto;
import com.kakaopay.recruite.conferenceroom.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void createUser(UserDto userDto) {
        UserDao userDao = UserDao.builder().userName(userDto.getUserName()).build();
        userRepository.save(userDao);
        userDto.setId(userDao.getId());
    }

    public Optional<UserDao> findUser(Long id) {
        return userRepository.findById(id);
    }

    public UserDto findUserByUserName(String userName) {
        UserDao userDao = userRepository.findByUserName(userName);
        return UserDto.builder().id(userDao.getId()).userName(userDao.getUserName()).build();
    }
}
