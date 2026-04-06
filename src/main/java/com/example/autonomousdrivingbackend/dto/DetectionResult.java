package com.example.autonomousdrivingbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResult {
    private String image;
    private String analysis;
    private Double yoloTime;
    private Double slmTime;
    private Double totalTime;
    private Integer detectionCount;
}