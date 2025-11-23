package com.example.capsulee_backend.capsule.repository;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapsuleRepository extends JpaRepository<Capsule, Long> {
    List<Capsule> findByCreatorOrderByOpenTimeDesc(User creator);
}
