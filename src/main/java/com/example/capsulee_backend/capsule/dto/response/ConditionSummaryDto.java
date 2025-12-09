package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionSummaryDto {

    private String type;
    private String value;
    private boolean matched;  // true | false

    public ConditionSummaryDto(String type, String value) {
        this.type = type;
        this.value = value;
        this.matched = false; // 또는 null 처리 가능하면 null
    }
}
