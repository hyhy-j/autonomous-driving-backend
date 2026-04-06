package com.example.autonomousdrivingbackend.consumer;

import com.example.autonomousdrivingbackend.dto.DetectionResult;
import com.example.autonomousdrivingbackend.service.DetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutonomousConsumer {

    private final DetectionService detectionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "autonomous-result", groupId = "autonomous-group")
    public void consume(String message) {
        try {
            DetectionResult result = objectMapper.readValue(message, DetectionResult.class);
            log.info("Kafka 메시지 수신: {}", result.getImage());
            detectionService.saveResult(result);
        } catch (Exception e) {
            log.error("메시지 처리 오류: {}", e.getMessage());
        }
    }
}
