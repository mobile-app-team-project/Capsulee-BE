package com.example.capsulee_backend.capsule.repository;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CapsuleRepository extends JpaRepository<Capsule, Long> {
    List<Capsule> findByCreatorOrderByOpenTimeDesc(User creator);
    Optional<Capsule> findTopByCreatorAndOpenTimeAfterOrderByOpenTimeAsc(User creator, LocalDateTime now);
}
