package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.domain.User;
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

    public User createUser(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .userName(userDto.getUserName())
                .build();
        userRepository.save(user);
        userDto.setId(user.getId());
        return user;
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public UserDto findUserByUserName(String userName) {
        return new UserDto(userRepository.findByUserName(userName));
    }
}
