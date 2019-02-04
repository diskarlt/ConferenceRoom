package com.kakaopay.recruite.conferenceroom;

import com.kakaopay.recruite.conferenceroom.dto.ReservationDto;
import com.kakaopay.recruite.conferenceroom.dto.RoomDto;
import com.kakaopay.recruite.conferenceroom.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ReservationControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;

    private List<RoomDto> roomDtoList;
    private RoomDto roomDto;
    private UserDto userDto;

    @Before
    public void before() {
        roomDtoList = Arrays.asList(restTemplate.getForObject("/rooms", RoomDto[].class));
        roomDto = roomDtoList.get(0);
        userDto = restTemplate.getForObject("/users/" + "사용자 A", UserDto.class);

        log.info(roomDtoList.toString());
        log.info(userDto.toString());
    }

    /**
     * 예약 생성 (1회성 예약)
     * 예약 : 10:00~10:30 (성공)
     */
    @Test
    public void test_create_onetime_reservation() {
        ReservationDto reservationDto = new ReservationDto(userDto, roomDto, 0, "2019-01-01", "10:00", "10:30");
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    /**
     * 예약 생성 (주 단위 반복 예약)
     * 예약 : 10:30~11:00, 3회 추가 반복 (성공)
     */
    @Test public void test_create_repeat_reservation() {
        ReservationDto reservationDto = new ReservationDto(userDto, roomDto, 0, "2019-01-02", "10:30", "11:00");
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    /**
     * 예약 요청 시간이 30분 단위가 아닌 경우
     * 예약 1 : 09:20 ~ 10:00 (실패)
     * 예약 2 : 09:30 ~ 10:10 (실패)
     */
    @Test
    public void test_create_bad_time_reservation() {
        ReservationDto reservationDto1 = new ReservationDto(userDto, roomDto, 0, "2019-01-03", "09:20", "10:00");
        ReservationDto reservationDto2 = new ReservationDto(userDto, roomDto, 0, "2019-01-03", "09:30", "11:10");

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationDto1, Void.class);
        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationDto2, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity2.getStatusCode());
    }

    /**
     * 예약 종료 시간이 예약 시작 시간보다 빠르거나 같은 경우
     * 예약 : 10:00 ~ 09:00 (실패)
     */
    @Test
    public void test_create_start_time_after_end_time_reservation() {
        ReservationDto reservationDto = new ReservationDto(userDto, roomDto, 0, "2019-01-04", "10:00", "09:00");
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    /**
     * 종료 시간과 시작 시간이 같은 경우에도 예약 성공
     * 예약 1 : 10:00 ~ 11:00 (성공)
     * 예약 2 : 11:00 ~ 12:00 (성공)
     */
    @Test
    public void test_create_continual_time_reservation() {
        ReservationDto reservationDto1 = new ReservationDto(userDto, roomDto, 0, "2019-01-05", "10:00", "11:00");
        ReservationDto reservationDto2 = new ReservationDto(userDto, roomDto, 0, "2019-01-05", "11:00", "12:00");

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationDto1, Void.class);
        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationDto2, Void.class);

        assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
    }

    /**
     * 시간이 겹치는 예약 생성은 실패
     * 예약 1 : 10:00 ~ 11:00 (성공)
     * 예약 2 : 09:00 ~ 10:30 (실패)
     */
    @Test
    public void test_create_conflict_reservation() {
        ReservationDto reservationDto1 = new ReservationDto(userDto, roomDto, 0, "2019-01-06", "10:00", "11:00");
        ReservationDto reservationDto2 = new ReservationDto(userDto, roomDto, 0, "2019-01-06", "09:00", "10:30");

        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationDto1, Void.class);
        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationDto2, Void.class);

        assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, responseEntity2.getStatusCode());
    }

    /**
     * 예약 조회
     */
    @Test
    public void test_find_reservation() {
        ReservationDto reservationDto = new ReservationDto(userDto, roomDto, 0, "2019-01-07", "10:00", "11:00");
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        List<ReservationDto> reservationDtoList = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=7", ReservationDto[].class));
        assertEquals(1, reservationDtoList.size());
        reservationDto.setId(reservationDtoList.get(0).getId());
        assertEquals(reservationDto, reservationDtoList.get(0));
    }

    /**
     * 예약이 하나도 없는 경우 조회
     */
    @Test
    public void test_find_empty_reservation() {
        List<ReservationDto> reservationDtoDataList = Arrays.asList(restTemplate.getForObject("/reservations?year=2018&month=1&day=1", ReservationDto[].class));
        assertTrue(reservationDtoDataList.isEmpty());
    }

    /**
     * 예약 취소
     */
    @Test
    public void test_cancel_reservation() {
        ReservationDto reservationDto = new ReservationDto(userDto, roomDto, 0, "2019-01-08", "10:00", "11:00");
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<ReservationDto> reservationDtoDataList1 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=8", ReservationDto[].class));
        assertEquals(1, reservationDtoDataList1.size());

        restTemplate.delete("/reservations/" + reservationDtoDataList1.get(0).getId());

        List<ReservationDto> reservationDtoDataList2 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=8", ReservationDto[].class));
        assertTrue(reservationDtoDataList2.isEmpty());
    }

    @Test
    public void test_reservation_after_cancel() {
        ReservationDto reservationDto = new ReservationDto(userDto, roomDto, 0, "2019-01-09", "10:00", "11:00");
        ResponseEntity<Void> responseEntity1 = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());

        List<ReservationDto> reservationDtoDataList1 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=9", ReservationDto[].class));
        assertEquals(1, reservationDtoDataList1.size());
        log.info(reservationDtoDataList1.toString());

        restTemplate.delete("/reservations/" + reservationDtoDataList1.get(0).getId());

        List<ReservationDto> reservationDtoDataList2 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=9", ReservationDto[].class));
        log.info(reservationDtoDataList2.toString());
        assertTrue(reservationDtoDataList2.isEmpty());

        ResponseEntity<Void> responseEntity2 = restTemplate.postForEntity("/reservations", reservationDto, Void.class);
        assertEquals(HttpStatus.OK, responseEntity2.getStatusCode());
        List<ReservationDto> reservationDtoDataList3 = Arrays.asList(restTemplate.getForObject("/reservations?year=2019&month=1&day=9", ReservationDto[].class));
        assertEquals(1, reservationDtoDataList3.size());
    }
}
