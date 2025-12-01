package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadyStatusResponseDto {
    private boolean isReady; // 수신자가 ready 버튼을 눌렀는지
}
