package com.example.capsulee_backend.capsule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadyStatusRequestDto {
    private boolean ready; // 수신자가 ready 버튼을 눌렀는지
}
