package com.example.capsulee_backend.capsule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantStatusDto {
    private Long userId;
    private String userName;
    private boolean ready;
    private String statusText;
}
