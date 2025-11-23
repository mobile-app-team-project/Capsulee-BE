package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CapsuleDetailResponseDto {

    private String status;  // LOCKED or WAITING or READY
    private Object capsuleDetail;

    // openTime의 당일 이전
    @Data
    @AllArgsConstructor
    public static class LockedCapsuleDetailDto {
        private CapsuleInfoDto.LockedCapsuleDto capsuleInfo;
        private List<ParticipantDto> participants;
        private List<ConditionSummaryDto> conditions;
    }

    // 오픈 당일인데 사용자가 Ready 버튼을 아직 누르지 않았을 때
    @Data
    @AllArgsConstructor
    public static class WaitingCapsuleDetailDto {
        private CapsuleInfoDto.WaitingCapsuleDto capsuleInfo;
        private List<ParticipantDto> participants;
        private List<ConditionSummaryDto> conditions;
    }

    // Ready인 경우
    @Data
    @AllArgsConstructor
    public static class ReadyCapsuleDetailDto {
        private CapsuleInfoDto.ReadyCapsuleDto capsuleInfo;
        private List<ParticipantDto> participants;
    }

    // 열린 경우
    @Data
    @AllArgsConstructor
    public static class OpenedCapsuleDetailDto {
        private CapsuleInfoDto.OpenedCapsuleDto capsuleInfo;
        private List<ParticipantDto> participants;
        private List<ConditionSummaryDto> conditions;
    }
}
