package com.example.capsulee_backend.user.repository;

import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByLoginID(String loginID);
    public Optional<User> findByLoginID(String loginID);
}
