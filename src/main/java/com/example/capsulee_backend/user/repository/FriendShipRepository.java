package com.example.capsulee_backend.user.repository;

import com.example.capsulee_backend.user.domain.FriendShip;
import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    public boolean existsBySenderAndReceiver(User sender, User receiver);
}
