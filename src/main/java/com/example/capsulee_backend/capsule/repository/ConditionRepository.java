package com.example.capsulee_backend.capsule.repository;

import com.example.capsulee_backend.capsule.domain.Conditions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Conditions, Long> {
}
