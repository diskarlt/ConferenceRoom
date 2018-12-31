package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 예약 생성 (1회성 예약)
     * 예약 : 10:00~10:30 (성공)
     */
    @Test
    public void test_create_onetime_reservation() {
        ReservationData reservationData = new ReservationData("회의실 A", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(10, 0), LocalTime.of(10, 30));
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationData, Void.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    /**
     * 예약 생성 (주 단위 반복 예약)
     * 예약 : 10:30~11:00, 3회 추가 반복 (성공)
     */
    @Test public void test_create_repeat_reservation() {
        ReservationData reservationData = new ReservationData("회의실 A", "사용자 A", "Subject", 3, LocalDate.of(2019, 1, 1), LocalTime.of(10, 30), LocalTime.of(11, 0));
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationData, Void.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    /**
     * 예약 요청 시간이 30분 단위가 아닌 경우
     * 예약 1 : 09:20 ~ 10:00 (실패)
     * 예약 2 : 09:30 ~ 10:10 (실패)
     */
    @Test
    public void test_create_bad_time_reservation() {
        ReservationData reservationData1 = new ReservationData("회의실 A", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(9, 20), LocalTime.of(10, 0));
        ReservationData reservationData2 = new ReservationData("회의실 A", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(9, 30), LocalTime.of(10, 10));

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationData1, Void.class);
        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationData2, Void.class);

        assertEquals(responseEntity1.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseEntity2.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 예약 종료 시간이 예약 시작 시간보다 빠르거나 같은 경우
     * 예약 : 10:00 ~ 09:00 (실패)
     */
    @Test
    public void test_create_start_time_after_end_time_reservation() {
        ReservationData reservationData = new ReservationData("회의실 A", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(10, 0), LocalTime.of(9, 0));
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationData, Void.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 종료 시간과 시작 시간이 같은 경우에도 예약 성공
     * 예약 1 : 10:00 ~ 11:00 (성공)
     * 예약 2 : 11:00 ~ 12:00 (성공)
     */
    @Test
    public void test_create_same_time_reservation() {
        ReservationData reservationData1 = new ReservationData("회의실 B", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(10, 0), LocalTime.of(11, 0));
        ReservationData reservationData2 = new ReservationData("회의실 B", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(11, 0), LocalTime.of(12, 0));

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationData1, Void.class);
        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationData2, Void.class);

        assertEquals(responseEntity1.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity2.getStatusCode(), HttpStatus.OK);
    }

    /**
     * 시간이 겹치는 예약 생성은 실패
     * 예약 1 : 10:00 ~ 11:00 (성공)
     * 예약 2 : 09:00 ~ 10:30 (실패)
     */
    @Test
    public void test_create_conflict_reservation() {
        ReservationData reservationData1 = new ReservationData("회의실 C", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(10, 0), LocalTime.of(11, 0));
        ReservationData reservationData2 = new ReservationData("회의실 C", "사용자 B", "Subject", 0, LocalDate.of(2019, 1, 1), LocalTime.of(9, 0), LocalTime.of(10, 30));

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationData1, Void.class);
        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationData2, Void.class);

        assertEquals(responseEntity1.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity2.getStatusCode(), HttpStatus.CONFLICT);
    }

    /**
     * 예약 조회
     */
    @Test
    public void test_find_reservation() {
        ReservationData reservationData = new ReservationData("회의실 D", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 2), LocalTime.of(10, 0), LocalTime.of(11, 0));

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationData, Void.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        List<ReservationData> reservationDataList = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=2", ReservationData[].class));
        assertEquals(reservationDataList.size(), 1);
        reservationData.setId(reservationDataList.get(0).getId());
        assertEquals(reservationData, reservationDataList.get(0));
    }

    /**
     * 예약이 하나도 없는 경우 조회
     */
    @Test
    public void test_find_empty_reservation() {
        List<ReservationData> reservationDataList = Arrays.asList(restTemplate.getForObject("/reservations?year=2018&month=1&day=1", ReservationData[].class));
        assertTrue(reservationDataList.isEmpty());
    }

    /**
     * 예약 취소
     */
    @Test
    public void test_cancel_reservation() {
        ReservationData reservationData = new ReservationData("회의실 D", "사용자 A", "Subject", 0, LocalDate.of(2019, 1, 3), LocalTime.of(10, 0), LocalTime.of(11, 0));

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationData, Void.class);
        assertEquals(responseEntity1.getStatusCode(), HttpStatus.OK);

        List<ReservationData> reservationDataList1 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=3", ReservationData[].class));
        assertEquals(reservationDataList1.size(), 1);

        restTemplate.delete("/reservations/" + reservationDataList1.get(0).getId());

        List<ReservationData> reservationDataList2 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=3", ReservationData[].class));
        assertTrue(reservationDataList2.isEmpty());
    }
}
