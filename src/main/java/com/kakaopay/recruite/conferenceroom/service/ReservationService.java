package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.domain.*;
import com.kakaopay.recruite.conferenceroom.dto.ReservationDto;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import com.kakaopay.recruite.conferenceroom.repository.ReservationStatusRepository;
import com.kakaopay.recruite.conferenceroom.repository.RoomRepository;
import com.kakaopay.recruite.conferenceroom.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationStatusRepository reservationStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    /**
     * 예약 생성
     * @param reservationDTO 생성할 예약 정보
     */
    @Transactional
    public void createReservation(User user, Room room, ReservationDto reservationDTO) {
        LocalDate date = LocalDate.of(
                Integer.valueOf(reservationDTO.getDate().split("-")[0]),
                Integer.valueOf(reservationDTO.getDate().split("-")[1]),
                Integer.valueOf(reservationDTO.getDate().split("-")[2])
        );
        LocalTime startTime = LocalTime.of(
                Integer.valueOf(reservationDTO.getStartTime().split(":")[0]),
                Integer.valueOf(reservationDTO.getStartTime().split(":")[1])
        );
        LocalTime endTime = LocalTime.of(
                Integer.valueOf(reservationDTO.getEndTime().split(":")[0]),
                Integer.valueOf(reservationDTO.getEndTime().split(":")[1])
        );

        Reservation reservation = Reservation.builder()
                .room(room)
                .user(user)
                .repeat(reservationDTO.getRepeat())
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date.plusWeeks(reservationDTO.getRepeat()))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        reservation = reservationRepository.save(reservation);

        for(int i=0; i<=reservationDTO.getRepeat(); ++i) {
            for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
                reservationStatusRepository.save(ReservationStatus.builder()
                        .date(date.plusWeeks(i))
                        .room(room)
                        .time(time)
                        .reservation(reservation)
                        .build());
            }
        }
    }

    /**
     * 해당 날짜에 등록된 예약 조회
     * @param date 조회할 날짜
     * @return 해당 날짜에 등록된 예약 List
     */
    public List<ReservationDto> findReservation(LocalDate date) {
        return reservationRepository.findAllByDateAndDayOfWeek(date, date.getDayOfWeek())
                .stream()
                .map(ReservationDto::new)
                .collect(toList());
    }

    /**
     * 예약 취소
     * @param id 취소하려는 예약의 id
     */
    @Transactional
    public void cancelReservation(Long id) {
        reservationStatusRepository.deleteByReservation_Id(id);
        reservationRepository.deleteById(id);
    }
}
