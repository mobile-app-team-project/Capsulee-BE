package com.example.capsulee_backend.capsule.dto.response;

import com.example.capsulee_backend.capsule.domain.Conditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleSummaryDto {

    private Long capsuleId;
    private String title;
    private String fromOrTo;
    private boolean opened;
    private List<ConditionSummaryDto> conditionSummaries;
}
