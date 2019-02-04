package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.dao.*;
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
import java.util.Optional;

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
    public synchronized void createReservation(UserDao userDao, RoomDao roomDao, ReservationDto reservationDTO) {
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

        ReservationDao reservationDao = ReservationDao.builder()
                .room(roomDao)
                .user(userDao)
                .repeat(reservationDTO.getRepeat())
                .dayOfWeek(date.getDayOfWeek())
                .startDate(date)
                .endDate(date.plusWeeks(reservationDTO.getRepeat()))
                .startTime(startTime)
                .endTime(endTime)
                .build();

        reservationDao = reservationRepository.save(reservationDao);

        for(int i=0; i<=reservationDTO.getRepeat(); ++i) {
            for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
                ReservationStatusDao reservationStatusDao = ReservationStatusDao.builder().date(date.plusWeeks(i)).room(roomDao).time(time).reservation(reservationDao).build();
                reservationStatusRepository.save(reservationStatusDao);
            }
        }
    }

    /**
     * 해당 날짜에 등록된 예약 조회
     * @param date 조회할 날짜
     * @return 해당 날짜에 등록된 예약 List
     */
    public List<ReservationDto> findReservation(LocalDate date) {
        List<ReservationDto> reservationDtoList = new ArrayList<>();
        reservationRepository.findAllByDateAndDayOfWeek(date, date.getDayOfWeek()).forEach(reservation -> reservationDtoList.add(new ReservationDto(reservation)));
        return reservationDtoList;
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
