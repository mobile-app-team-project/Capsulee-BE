package com.example.capsulee_backend.capsule.repository;

import com.example.capsulee_backend.capsule.domain.Conditions;
import com.example.capsulee_backend.capsule.domain.RecipientConditions;
import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipientConditionsRepository extends JpaRepository<RecipientConditions, Long> {
    Optional<RecipientConditions> findByRecipientAndCondition(User user, Conditions condition);
}
