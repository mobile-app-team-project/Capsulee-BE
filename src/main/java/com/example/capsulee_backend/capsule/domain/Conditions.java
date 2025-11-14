package com.example.capsulee_backend.capsule.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Conditions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "capsule_id")
    private Capsule capsule;

    @Enumerated(EnumType.STRING)
    private ConditionType type; // 조건 종류
    private String value; // 조건 값

    @OneToMany(mappedBy = "condition")
    private List<RecipientConditions> recipientConditions = new ArrayList<>(); // 해당 조건을 만족해야 하는 사람들
}
