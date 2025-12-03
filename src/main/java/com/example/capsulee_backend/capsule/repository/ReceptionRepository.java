package com.example.capsulee_backend.capsule.repository;

import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceptionRepository extends JpaRepository<Reception, Long> {
    List<Reception> findByRecipient(User currentUser);

    Optional<Reception> findByCapsuleAndRecipient(Capsule capsule, User user);
}
