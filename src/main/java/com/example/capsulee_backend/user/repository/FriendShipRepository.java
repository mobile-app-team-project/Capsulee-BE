package com.example.capsulee_backend.user.repository;

import com.example.capsulee_backend.user.domain.FriendShip;
import com.example.capsulee_backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    public Optional<FriendShip> findById(Long id);
    public boolean existsBySenderAndReceiver(User sender, User receiver);
    public FriendShip findBySenderAndReceiver(User sender, User receiver);
    public List<FriendShip> findAllByReceiver(User receiver);
    public List<FriendShip> findAllBySender(User sender);
    public int countByReceiver(User receiver);
    public int countBySender(User sender);
}
