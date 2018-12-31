package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.domain.Reservation;
import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReservationService {
    @Autowired
    ReservationRepository reservationRepository;

    /**
     * 예약 생성
     * 기존 예약과의 충돌이 나는 경우 예약 생성은 실패한다.
     * @return 예약 성공 여부
     */
    public boolean createReservation(ReservationData reservationData) {
        if(reservationRepository.isConflict(reservationData.getRoomName(),
                reservationData.getStartDate(),
                reservationData.getEndDate(),
                reservationData.getDayOfWeek(),
                reservationData.getStartTime(),
                reservationData.getEndTime()))
            return false;

        reservationRepository.save(reservationData);
        return true;
    }

    /**
     * 해당 날짜에 등록된 예약 조회
     * @return 해당 날짜에 등록된 예약 List
     */
    public List<ReservationData> findReservation(LocalDate date) {
        return reservationRepository.findAllByDateAndDayOfWeek(date, date.getDayOfWeek());
    }

    /**
     * 예약 취소
     * 취소하려는 ID의 예약이 없는 경우 예약 취소는 실패한다.
     * @return 예약 취소 여부
     */
    public boolean cancelReservation(Long id) {
        if(reservationRepository.existsById(id) == false)
            return false;
        reservationRepository.deleteById(id);
        return true;
    }

}
