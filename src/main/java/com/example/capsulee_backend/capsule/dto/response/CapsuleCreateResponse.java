package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleCreateResponse {

    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime openTime;
    private List<Long> recipientIds;
    private List<ConditionRequest> conditions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionRequest {
        private String type;  // "GEO", "EVENT", "ACTION"
        private String value;
    }
}
