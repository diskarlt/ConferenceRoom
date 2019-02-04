package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.dao.RoomDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomDao, Long> {
}
