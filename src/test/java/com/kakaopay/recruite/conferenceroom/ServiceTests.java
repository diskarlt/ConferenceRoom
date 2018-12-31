package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import com.kakaopay.recruite.conferenceroom.service.ReservationService;
import org.junit.Before;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTests {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Before
    public void before() {

    }

    /**
     * 예약 생성 (1회성 예약)
     */
    @Test
    public void test_create_onetime_reservation() {
        String roomName = "회의실 A";
        String userName = "사용자 A";
        String subject = "Subject";
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10,0);
        LocalTime endTime = LocalTime.of(11,30);
        int repeat = 0;

        ReservationData reservationData = new ReservationData(roomName, userName, subject, repeat, date, startTime, endTime);
        when(reservationRepository.isConflict(anyString(), any(LocalDate.class), any(LocalDate.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(reservationRepository.save(any(ReservationData.class))).thenReturn(reservationData);

        assertTrue(reservationService.createReservation(reservationData));

        verify(reservationRepository).isConflict(anyString(), any(LocalDate.class), any(LocalDate.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class));
        verify(reservationRepository).save(any(ReservationData.class));
    }

    /**
     * 예약 생성 (주 단위 반복 예약)
     */
    @Test
    public void test_create_repeat_reservation() {
        String roomName = "회의실 A";
        String userName = "사용자 A";
        String subject = "Subject";
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10,0);
        LocalTime endTime = LocalTime.of(11,30);
        int repeat = 3;

        ReservationData reservationData = new ReservationData(roomName, userName, subject, repeat, date, startTime, endTime);
        when(reservationRepository.isConflict(anyString(), any(LocalDate.class), any(LocalDate.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(false);
        when(reservationRepository.save(any(ReservationData.class))).thenReturn(reservationData);

        assertTrue(reservationService.createReservation(reservationData));

        verify(reservationRepository).isConflict(anyString(), any(LocalDate.class), any(LocalDate.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class));
        verify(reservationRepository).save(any(ReservationData.class));
    }

    /**
     * 시간이 겹치는 예약 생성은 실패
     */
    @Test
    public void test_create_conflict_reservation() {
        String roomName = "회의실 A";
        String userName = "사용자 A";
        String subject = "Subject";
        LocalDate date = LocalDate.of(2019, 1, 1);
        LocalTime startTime = LocalTime.of(10,0);
        LocalTime endTime = LocalTime.of(11,30);
        int repeat = 3;

        ReservationData reservationData = new ReservationData(roomName, userName, subject, repeat, date, startTime, endTime);
        when(reservationRepository.isConflict(anyString(), any(LocalDate.class), any(LocalDate.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class))).thenReturn(true);

        assertFalse(reservationService.createReservation(reservationData));

        verify(reservationRepository).isConflict(anyString(), any(LocalDate.class), any(LocalDate.class), any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class));
        verify(reservationRepository, never()).save(any(ReservationData.class));
    }

    /**
     * 예약 조회
     */
    @Test
    public void test_find_reservation() {
        String subject = "Subject";
        LocalDate date = LocalDate.of(2019, 1, 1);
        int repeat = 3;

        ReservationData reservationData1 = new ReservationData("회의실 A", "사용자 A", subject, 0, date, LocalTime.of(10,0), LocalTime.of(11,30));
        ReservationData reservationData2 = new ReservationData("회의실 B", "사용자 B", subject, repeat, date, LocalTime.of(11,0), LocalTime.of(12, 0));
        reservationData1.setId(1L);
        reservationData2.setId(2L);
        List<ReservationData> reservationDataList = new ArrayList<>();
        reservationDataList.add(reservationData1);
        reservationDataList.add(reservationData2);
        when(reservationRepository.findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class))).thenReturn(reservationDataList);

        List<ReservationData> list = reservationService.findReservation(date);
        assertEquals(list.size(), 2);
        assertEquals(list, reservationDataList);

        verify(reservationRepository).findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class));
    }

    /**
     * 예약이 하나도 없는 경우 조회
     */
    @Test
    public void test_not_found_reservation() {
        LocalDate date = LocalDate.of(2019, 1, 1);

        List<ReservationData> list = new ArrayList<>();
        list.clear();
        when(reservationRepository.findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class))).thenReturn(list);

        List<ReservationData> reservationDataList = reservationService.findReservation(date);
        assertTrue(reservationDataList.isEmpty());

        verify(reservationRepository).findAllByDateAndDayOfWeek(any(LocalDate.class), any(DayOfWeek.class));
    }

    /**
     * 예약 취소
     */
    @Test
    public void test_cancel_reservation() {
        when(reservationRepository.existsById(1L)).thenReturn(true);

        assertTrue(reservationService.cancelReservation(1L));

        verify(reservationRepository).existsById(1L);
        verify(reservationRepository).deleteById(1L);
    }

    /**
     * 취소하려는 예약이 없는 경우
     */
    @Test
    public void test_not_found_cancel_reservation() {
        when(reservationRepository.existsById(1L)).thenReturn(false);

        assertFalse(reservationService.cancelReservation(1L));

        verify(reservationRepository).existsById(1L);
    }
}
