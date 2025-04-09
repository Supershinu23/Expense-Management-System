package com.ems.auth.repository;

import com.ems.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByMobile(String mobileNumber);

    Optional<User> findByUsername(String userName);
}
