package com.example.capsulee_backend.user.domain;


import com.example.capsulee_backend.capsule.domain.Capsule;
import com.example.capsulee_backend.capsule.domain.Reception;
import com.example.capsulee_backend.capsule.domain.RecipientConditions;
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

    private boolean isOkAlarm; // 사용자가 알람을 받는지 여부

    @OneToMany(mappedBy = "sender")
    private List<FriendShip> sentFriendShips = new ArrayList<>(); // 사용자가 보낸 친구 신청

    @OneToMany(mappedBy = "receiver")
    private List<FriendShip> receivedFriendShips = new ArrayList<>(); // 사용자가 받은 친구 신청

    @OneToMany(mappedBy = "creator")
    private List<Capsule> myCapsules = new ArrayList<>(); // 사용자가 만든 캡슐

    @OneToMany(mappedBy = "recipient")
    private List<Reception> receptions = new ArrayList<>(); // 사용자가 수신한 캡슐

    @OneToMany(mappedBy = "recipient")
    private List<RecipientConditions> recipientConditions = new ArrayList<>(); // 사용자가 만족해야할 캡슐 조건

    @Builder
    public User (String loginID, String password, String username, boolean isOkAlarm) {
        this.loginID = loginID;
        this.password = password;
        this.username = username;
        this.isOkAlarm = isOkAlarm;
    }
}
