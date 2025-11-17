package com.example.capsulee_backend.capsule.repository;

import com.example.capsulee_backend.capsule.domain.Capsule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapsuleRepository extends JpaRepository<Capsule, Long> {
}
