package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.domain.Reservation;
import com.kakaopay.recruite.conferenceroom.domain.ReservationStatus;
import com.kakaopay.recruite.conferenceroom.domain.Room;
import com.kakaopay.recruite.conferenceroom.domain.User;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import com.kakaopay.recruite.conferenceroom.repository.ReservationStatusRepository;
import com.kakaopay.recruite.conferenceroom.repository.RoomRepository;
import com.kakaopay.recruite.conferenceroom.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j
public class ReservationRepositoryTests {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationStatusRepository reservationStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    Long roomId;
    Long userId;

    @Before
    public void before() {
        roomId = roomRepository.findAll().get(0).getId();
        userId = userRepository.findAll().get(0).getId();
    }

    /**
     * 1회성 예약
     */
    @Test
    public void test_create_onetime_reservation() {
        Room room = roomRepository.findById(roomId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        // 예약 등록
        LocalDate date = LocalDate.of(2019, 1, 1);
        Reservation reservation = Reservation.builder()
                .room(room)
                .user(user)
                .repeat(0)
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date)
                .startTime(LocalTime.of(13, 30))
                .endTime(LocalTime.of(14, 30))
                .build();
        reservationRepository.save(reservation);

        // 예약 조회
        Reservation findReservation = reservationRepository.findById(reservation.getId()).orElse(null);

        // 비교
        assertEquals(room, findReservation.getRoom());
        assertEquals(user, findReservation.getUser());
        assertEquals(reservation, findReservation);
    }

    /**
     * 반복 예약
     */
    @Test
    public void test_create_repeat_reservation() {
        // 예약 등록
        int repeat = 3;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = startDate.plusWeeks(3);
        LocalTime startTime = LocalTime.of(10, 30);
        LocalTime endTime = LocalTime.of(11, 30);
        Reservation reservation = Reservation.builder()
                .repeat(repeat)
                .dayOfWeek(startDate.getDayOfWeek())
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        reservationRepository.save(reservation);

        // 예약 조회
        Reservation data = reservationRepository.findById(reservation.getId()).orElse(null);

        // 비교
        assertEquals(reservation, data);
    }

    /**
     * 예약 상태 등록
     */
    @Test
    public void test_reservation_status() {
        Room room = roomRepository.findById(roomId).orElse(null);

        int repeat = 3;
        LocalDate startDate = LocalDate.of(2019, 1, 1);
        LocalDate endDate = startDate.plusWeeks(3);
        LocalTime startTime = LocalTime.of(10, 30);
        LocalTime endTime = LocalTime.of(11, 30);
        Reservation reservation = Reservation.builder()
                .repeat(repeat)
                .dayOfWeek(startDate.getDayOfWeek())
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        reservationRepository.save(reservation);

        for(int i=0; i<=repeat; ++i) {
            for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
                ReservationStatus reservationStatus = ReservationStatus.builder().date(startDate.plusWeeks(i)).room(room).time(time).reservation(reservation).build();
                reservationStatusRepository.save(reservationStatus);
            }
        }

        for(int i=0; i<=repeat; ++i) {
            for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
                assertTrue(reservationStatusRepository.existsByRoom_IdAndDateAndTime(room.getId(), startDate.plusWeeks(i), time));
            }
        }
    }
}
