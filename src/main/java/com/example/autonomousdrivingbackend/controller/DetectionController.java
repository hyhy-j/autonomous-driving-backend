package com.example.autonomousdrivingbackend.controller;

import com.example.autonomousdrivingbackend.dto.DetectionResult;
import com.example.autonomousdrivingbackend.service.DetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/detection")
@RequiredArgsConstructor
public class DetectionController {

    private final DetectionService detectionService;

    @GetMapping("/{imageName}")
    public ResponseEntity<DetectionResult> getResult(@PathVariable String imageName) {
        log.info("결과 조회 요청: {}", imageName);
        DetectionResult result = detectionService.getResult(imageName);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
