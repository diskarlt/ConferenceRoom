package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.domain.Room;
import com.kakaopay.recruite.conferenceroom.domain.User;
import com.kakaopay.recruite.conferenceroom.repository.RoomRepository;
import com.kakaopay.recruite.conferenceroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Application implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoomRepository roomRepository;

	public static void main(String[] args) {	
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.save(User.builder().id(1000L).userName("사용자 A").build());
		userRepository.save(User.builder().id(1001L).userName("사용자 B").build());
		userRepository.save(User.builder().id(1002L).userName("사용자 C").build());

		roomRepository.save(Room.builder().roomName("회의실 A").build());
		roomRepository.save(Room.builder().roomName("회의실 B").build());
		roomRepository.save(Room.builder().roomName("회의실 C").build());
		roomRepository.save(Room.builder().roomName("회의실 D").build());
		roomRepository.save(Room.builder().roomName("회의실 E").build());
		roomRepository.save(Room.builder().roomName("회의실 F").build());
		roomRepository.save(Room.builder().roomName("회의실 G").build());
	}

}
