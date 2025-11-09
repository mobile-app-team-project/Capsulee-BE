package com.example.capsulee_backend.user.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginID;
    private String password;
    private String username;

    @OneToMany(mappedBy = "sender")
    private List<FriendShip> sentFriendShips = new ArrayList<>(); // 사용자가 보낸 친구 신청

    @OneToMany(mappedBy = "receiver")
    private List<FriendShip> receivedFriendShips = new ArrayList<>(); // 사용자가 받은 친구 신청

    @Builder
    public User (String loginID, String password, String username) {
        this.loginID = loginID;
        this.password = password;
        this.username = username;
    }
}
