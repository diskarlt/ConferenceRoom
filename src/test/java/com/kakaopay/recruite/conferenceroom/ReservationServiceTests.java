package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.dao.*;
import com.kakaopay.recruite.conferenceroom.dto.ReservationDto;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import com.kakaopay.recruite.conferenceroom.repository.ReservationStatusRepository;
import com.kakaopay.recruite.conferenceroom.repository.RoomRepository;
import com.kakaopay.recruite.conferenceroom.repository.UserRepository;
import com.kakaopay.recruite.conferenceroom.service.ReservationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTests {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationStatusRepository reservationStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomRepository roomRepository;

    /**
     * 예약 생성 (1회성 예약)
     */
    @Test
    public void test_create_onetime_reservation() {
        String roomName = "회의실 A";
        String userName = "사용자 A";
        RoomDao roomDao = RoomDao.builder().id(1L).roomName(roomName).build();
        UserDao userDao = UserDao.builder().id(1L).userName(userName).build();
        int repeat = 0;
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 30);

        ReservationDao reservationDao = ReservationDao.builder()
                .id(1L)
                .user(userDao)
                .room(roomDao)
                .repeat(repeat)
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        when(reservationRepository.save(any(ReservationDao.class))).thenReturn(reservationDao);
        when(reservationStatusRepository.save(any(ReservationStatusDao.class)))
                .thenReturn(ReservationStatusDao.builder().id(1L).date(date).room(roomDao).time(startTime).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(2L).date(date).room(roomDao).time(startTime.plusMinutes(30)).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(3L).date(date).room(roomDao).time(startTime.plusMinutes(60)).reservation(reservationDao).build());

        ReservationDto reservationDto = new ReservationDto(reservationDao);
        reservationService.createReservation(userDao, roomDao, reservationDto);

        verify(reservationRepository).save(any(ReservationDao.class));
        // 9:00 ~ 10:30 의 경우 30분 단위 구간으로 9:00~9:30, 9:30~10:00, 10:00~10:30을 예약 상태로 저장한다.
        verify(reservationStatusRepository, times(3)).save(any(ReservationStatusDao.class));
    }

    /**
     * 예약 생성 (주 단위 반복 예약)
     */
    @Test
    public void test_create_repeat_reservation() {
        String roomName = "회의실 A";
        String userName = "사용자 A";
        RoomDao roomDao = RoomDao.builder().id(1L).roomName(roomName).build();
        UserDao userDao = UserDao.builder().id(1L).userName(userName).build();
        int repeat = 2;
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 30);

        ReservationDao reservationDao = ReservationDao.builder()
                .id(1L)
                .user(userDao)
                .room(roomDao)
                .repeat(repeat)
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        when(reservationRepository.save(any(ReservationDao.class))).thenReturn(reservationDao);
        when(reservationStatusRepository.save(any(ReservationStatusDao.class)))
                .thenReturn(ReservationStatusDao.builder().id(1L).date(date).room(roomDao).time(startTime).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(2L).date(date).room(roomDao).time(startTime.plusMinutes(30)).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(3L).date(date).room(roomDao).time(startTime.plusMinutes(60)).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(4L).date(date.plusWeeks(1)).room(roomDao).time(startTime).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(5L).date(date.plusWeeks(1)).room(roomDao).time(startTime.plusMinutes(30)).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(6L).date(date.plusWeeks(1)).room(roomDao).time(startTime.plusMinutes(60)).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(7L).date(date.plusWeeks(2)).room(roomDao).time(startTime).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(8L).date(date.plusWeeks(2)).room(roomDao).time(startTime.plusMinutes(30)).reservation(reservationDao).build())
                .thenReturn(ReservationStatusDao.builder().id(9L).date(date.plusWeeks(2)).room(roomDao).time(startTime.plusMinutes(60)).reservation(reservationDao).build());

        ReservationDto reservationDto = new ReservationDto(reservationDao);
        reservationService.createReservation(userDao, roomDao, reservationDto);

        verify(reservationRepository).save(any(ReservationDao.class));
        // 9:00 ~ 10:30 의 경우 30분 단위 구간으로 9:00~9:30, 9:30~10:00, 10:00~10:30을 예약 상태로 저장한다.
        verify(reservationStatusRepository, times(3*3)).save(any(ReservationStatusDao.class));
    }

    /**
     * 예약 조회
     */
    @Test
    public void test_find_reservation() {
        LocalDate date = LocalDate.of(2019, 1, 1);
        UserDao userDao = UserDao.builder().id(1L).userName("사용자 A").build();
        RoomDao roomDao = RoomDao.builder().id(1L).roomName("회의실 A").build();

        ReservationDao reservationDao1 = ReservationDao.builder()
                .id(1L)
                .user(userDao)
                .room(roomDao)
                .repeat(0)
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date)
                .startTime(LocalTime.of(10,0))
                .endTime(LocalTime.of(11,30))
                .build();
        ReservationDao reservationDao2 = ReservationDao.builder()
                .id(2L)
                .user(userDao)
                .room(roomDao)
                .repeat(2)
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date)
                .startTime(LocalTime.of(11,30))
                .endTime(LocalTime.of(12,0))
                .build();
        List<ReservationDao> reservationDaoList = new ArrayList<>();
        reservationDaoList.add(reservationDao1);
        reservationDaoList.add(reservationDao2);
        when(reservationRepository.findAllByDateAndDayOfWeek(date, date.getDayOfWeek())).thenReturn(reservationDaoList);

        List<ReservationDto> list = reservationService.findReservation(date);
        assertEquals(2, list.size());
        assertEquals(reservationDao1.getId(), list.get(0).getId());
        assertEquals(reservationDao1.getUser().getId(), list.get(0).getUser().getId());
        assertEquals(reservationDao1.getUser().getUserName(), list.get(0).getUser().getUserName());
        assertEquals(reservationDao1.getRoom().getId(), list.get(0).getRoom().getId());
        assertEquals(reservationDao1.getRoom().getRoomName(), list.get(0).getRoom().getRoomName());
        assertEquals(reservationDao1.getRepeat(), list.get(0).getRepeat());
        assertEquals("2019-01-01", list.get(0).getDate());
        assertEquals("10:00", list.get(0).getStartTime());
        assertEquals("11:30", list.get(0).getEndTime());
        assertEquals(reservationDao2.getId(), list.get(1).getId());
        assertEquals(reservationDao2.getUser().getId(), list.get(1).getUser().getId());
        assertEquals(reservationDao2.getUser().getUserName(), list.get(1).getUser().getUserName());
        assertEquals(reservationDao2.getRoom().getId(), list.get(1).getRoom().getId());
        assertEquals(reservationDao2.getRoom().getRoomName(), list.get(1).getRoom().getRoomName());
        assertEquals(reservationDao2.getRepeat(), list.get(1).getRepeat());
        assertEquals("2019-01-01", list.get(1).getDate());
        assertEquals("11:30", list.get(1).getStartTime());
        assertEquals("12:00", list.get(1).getEndTime());

        verify(reservationRepository).findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class));
    }

    /**
     * 예약이 하나도 없는 경우 조회
     */
    @Test
    public void test_not_found_reservation() {
        LocalDate date = LocalDate.of(2019, 1, 1);

        List<ReservationDao> list = new ArrayList<>();
        list.clear();
        when(reservationRepository.findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class))).thenReturn(list);

        List<ReservationDto> reservationList = reservationService.findReservation(date);
        assertTrue(reservationList.isEmpty());

        verify(reservationRepository).findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class));
    }

    /**
     * 예약 취소
     */
    @Test
    public void test_cancel_reservation() {
        reservationService.cancelReservation(1L);

        verify(reservationStatusRepository).deleteByReservation_Id(1L);
    }
}
