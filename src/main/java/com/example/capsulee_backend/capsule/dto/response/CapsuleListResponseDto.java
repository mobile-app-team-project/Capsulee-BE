package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleListResponseDto {

    private CapsuleStatsDto stats;
    private List<CapsuleSummaryDto> capsule;
}
