package com.example.capsulee_backend.capsule.dto.request;

import lombok.Getter;

@Getter
public class LocationConditionRequestDto {

    private Long capsuleId;
    private Double latitude;
    private Double longitude;
}
