package com.example.capsulee_backend.capsule.domain;

import com.example.capsulee_backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Reception {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reception_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id")
    private Capsule capsule; // 캡슐

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient; // 수신자 유저

    @Setter
    private boolean isReady;  // 수신자의 ready 여부

    public Reception(Capsule capsule, User recipient) {
        this.capsule = capsule;
        this.recipient = recipient;
        this.isReady = false;
    }
}
