package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import com.kakaopay.recruite.conferenceroom.domain.Reservation;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTests {
    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * 1회성 예약
     */
    @Test
    public void test_create_onetime_reservation() {
        // 예약 등록
        Reservation request = new Reservation("회의실 A", "사용자 A", 0, "2019-01-01", "13:30", "14:30");
        ReservationData reservationData = new ReservationData(request);
        reservationRepository.save(reservationData);

        // 예약 조회
        ReservationData data = reservationRepository.findById(reservationData.getId()).orElse(null);

        // 비교
        assertEquals(reservationData, data);
    }

    /**
     * 반복 예약
     */
    @Test
    public void test_create_repeat_reservation() {
        // 예약 등록
        Reservation request = new Reservation("회의실 A", "사용자 A", 3, "2019-01-01", "10:30", "11:30");
        ReservationData reservationData = new ReservationData(request);
        reservationRepository.save(reservationData);

        // 예약 조회
        ReservationData data = reservationRepository.findById(reservationData.getId()).orElse(null);

        // 비교
        assertEquals(reservationData, data);
    }

    /**
     * 중첩된 예약이 있는지 확인
     */
    @Test
    public void test_not_conflict() {
        String roomName = "회의실 A";
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10,0);
        LocalTime endTime = LocalTime.of(11,30);
        assertFalse(reservationRepository.isConflict(roomName, date, date, date.getDayOfWeek(), startTime, endTime));
        assertFalse(reservationRepository.isConflict(roomName, date, date.plusWeeks(2L), date.getDayOfWeek(), startTime, endTime));
    }

    /**
     * 1회성 예약이 등록되어 있는 상태에서 예약을 추가로 등록할때 기존의 예약과의 충돌 가능성에 대해 확인한다.
     */
    @Test
    public void test_check_conflict_on_onetime_reservation() {
        String roomName = "회의실 A";
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 30);

        // 예약 등록
        Reservation request = new Reservation("회의실 A", "사용자 A", 3, "2019-01-01", "10:00", "11:30");
        ReservationData reservationData = new ReservationData(request);
        reservationRepository.save(reservationData);

        for(int repeat=0; repeat<3; ++repeat) { // 1회성 및 반복성 예약을 모두 확인한다. 2주 정도까지 확인한다.

            // 회의실이 다른 경우에는 중첩되지 않는다.
            assertFalse(reservationRepository.isConflict("회의실 B", date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime));

            // 시작, 종료 시간이 같지만 겹치는 시간이 없는 경우에는 중첩되지 않는다.
            assertFalse(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), startTime));
            assertFalse(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), endTime, endTime.plusMinutes(30)));

            // 다른 요일의 같은 시간의 예약의 경우에는 중첩되지 않는다.
            assertFalse(reservationRepository.isConflict(roomName, date.minusDays(1), date.plusWeeks(repeat), date.minusDays(1).getDayOfWeek(), startTime, endTime));
            assertFalse(reservationRepository.isConflict(roomName, date.plusDays(1), date.plusWeeks(repeat), date.plusDays(1).getDayOfWeek(), startTime, endTime));

            // 시간이 겹치는 경우에는 중첩된 것으로 판단한다.
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), endTime.minusMinutes(30)));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), endTime));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), endTime.plusMinutes(30)));

            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime.minusMinutes(30)));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime.plusMinutes(30)));

            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.plusMinutes(30), endTime.minusMinutes(30)));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.plusMinutes(30), endTime));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.plusMinutes(30), endTime.plusMinutes(30)));
        }
    }

    /**
     * 반복성 예약이 등록되어 있는 상태에서 예약을 추가로 등록할때 기존의 예약과의 충돌 가능성에 대해 확인한다.
     */
    @Test
    public void test_check_conflict_on_repeat_reservation() {
        String roomName = "회의실 A";
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 30);

        // 예약 등록
        Reservation request = new Reservation("회의실 A", "사용자 A", 3, "2019-01-01", "10:00", "11:30");
        ReservationData reservationData = new ReservationData(request);
        reservationRepository.save(reservationData);

        for(int repeat=0; repeat<3; ++repeat) { // 1회성 및 반복성 예약을 모두 확인한다. 2주 정도까지 확인한다.

            // 회의실이 다른 경우에는 중첩되지 않는다.
            assertFalse(reservationRepository.isConflict("회의실 B", date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime));

            // 시작, 종료 시간이 같지만 겹치는 시간이 없는 경우에는 중첩되지 않는다.
            assertFalse(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), startTime));
            assertFalse(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), endTime, endTime.plusMinutes(30)));

            // 다른 요일의 같은 시간의 예약의 경우에는 중첩되지 않는다.
            assertFalse(reservationRepository.isConflict(roomName, date.minusDays(1), date.plusWeeks(repeat), date.minusDays(1).getDayOfWeek(), startTime, endTime));
            assertFalse(reservationRepository.isConflict(roomName, date.plusDays(1), date.plusWeeks(repeat), date.plusDays(1).getDayOfWeek(), startTime, endTime));

            // 시간이 겹치는 경우에는 중첩된 것으로 판단한다.
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), endTime.minusMinutes(30)));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), endTime));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.minusMinutes(30), endTime.plusMinutes(30)));

            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime.minusMinutes(30)));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime, endTime.plusMinutes(30)));

            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.plusMinutes(30), endTime.minusMinutes(30)));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.plusMinutes(30), endTime));
            assertTrue(reservationRepository.isConflict(roomName, date, date.plusWeeks(repeat), date.getDayOfWeek(), startTime.plusMinutes(30), endTime.plusMinutes(30)));
        }
    }
}
