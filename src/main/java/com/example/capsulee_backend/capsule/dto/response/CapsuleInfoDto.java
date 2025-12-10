package com.example.capsulee_backend.capsule.dto.response;

import lombok.Builder;

public class CapsuleInfoDto {

    // openTime 이전
    @Builder
    public record LockedCapsuleDto(
            Long capsuleId,
            String title,
            String from,
            String openTime,
            int processPercent   // 캡슐 열림까지 남은 시간에 대한 진행률
    ) {}

    @Builder
    public record WaitingCapsuleDto(
            Long capsuleId,
            String title,
            String from,
            String openTime
    ) {}

    @Builder
    public record ReadyCapsuleDto(
            Long capsuleId,
            String title,
            String from,
            String openTime
    ) {}

    @Builder
    public record OpenedCapsuleDto(
            Long capsuleId,
            String title,
            String from,
            String openTime,
            String content,
            String imageUrl
    ) {}
}
