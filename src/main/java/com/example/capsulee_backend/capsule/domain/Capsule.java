package com.example.capsulee_backend.capsule.domain;

import com.example.capsulee_backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Capsule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "capsule_id")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    private String title; // 캡슐 제목
    private String content; // 캡슐 내용
    private String imageURL;

    private LocalDateTime openTime; // 열릴 시간
    private boolean isOpened; // 캡슐이 열렸는지

    @OneToMany(mappedBy = "capsule")
    private List<Reception> receptions; // 이 캡슐이 수신된 정보

    @OneToMany(mappedBy = "capsule")
    private List<Conditions> condition; // 이 캡슐이 열리는 데에 필요한 조건
}
