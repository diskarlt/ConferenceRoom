package com.kakaopay.recruite.conferenceroom.repository;

import com.kakaopay.recruite.conferenceroom.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDao, Long> {
    UserDao findByUserName(String userName);
}
