package com.kakaopay.recruite.conferenceroom.service;

import com.kakaopay.recruite.conferenceroom.domain.ReservationData;
import com.kakaopay.recruite.conferenceroom.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * 예약 생성
     * 기존 예약과의 충돌이 나는 경우 예약 생성은 실패한다.
     * @param reservationData 생성할 예약 정보
     * @throws Exception 기존 예약과 충돌나는 경우 발생
     */
    @Transactional
    public synchronized void createReservation(ReservationData reservationData) throws Exception {
        if(reservationRepository.isConflict(reservationData.getRoomName(),
                reservationData.getStartDate(),
                reservationData.getEndDate(),
                reservationData.getDayOfWeek(),
                reservationData.getStartTime(),
                reservationData.getEndTime()))
            throw new Exception("Occurred Reservation Conflict");

        reservationData = reservationRepository.save(reservationData);

        if(reservationRepository.isConflictExcludeSelf(reservationData.getId(),
                reservationData.getRoomName(),
                reservationData.getStartDate(),
                reservationData.getEndDate(),
                reservationData.getDayOfWeek(),
                reservationData.getStartTime(),
                reservationData.getEndTime())) {
            throw new Exception("Occurred Reservation Conflict");
        }
    }

    /**
     * 해당 날짜에 등록된 예약 조회
     * @param date 조회할 날짜
     * @return 해당 날짜에 등록된 예약 List
     */
    public List<ReservationData> findReservation(LocalDate date) {
        return reservationRepository.findAllByDateAndDayOfWeek(date, date.getDayOfWeek());
    }

    /**
     * 예약 취소
     * 취소하려는 ID의 예약이 없는 경우 예약 취소는 실패한다.
     * @param id 취소하려는 예약의 id
     * @return 예약 취소 여부
     */
    public boolean cancelReservation(Long id) {
        if(!reservationRepository.existsById(id))
            return false;
        reservationRepository.deleteById(id);
        return true;
    }

}
