package com.example.autonomousdrivingbackend.consumer;

import com.example.autonomousdrivingbackend.dto.DetectionResult;
import com.example.autonomousdrivingbackend.service.DetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutonomousConsumer implements ConsumerSeekAware {

    private final DetectionService detectionService;
    private final ObjectMapper objectMapper;
    private final AtomicLong lastProcessedTime = new AtomicLong(0);
    private static final long MIN_INTERVAL_MS = 100; // 최소 100ms 간격

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        // 시작 시 최신 오프셋으로 이동
        assignments.forEach((partition, offset) -> callback.seekToEnd(partition.topic(), partition.partition()));
    }

    @KafkaListener(topics = "autonomous-result", groupId = "autonomous-group")
    public void consume(ConsumerRecord<String, String> record,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        try {
            long now = System.currentTimeMillis();

            // 이전 처리로부터 MIN_INTERVAL_MS 이내면 스킵
            if (now - lastProcessedTime.get() < MIN_INTERVAL_MS) {
                log.info("오래된 프레임 스킵: {}", record.value());
                return;
            }

            lastProcessedTime.set(now);
            DetectionResult result = objectMapper.readValue(record.value(), DetectionResult.class);
            log.info("Kafka 메시지 수신: {}", result.getImage());
            detectionService.saveResult(result);

        } catch (Exception e) {
            log.error("메시지 처리 오류: {}", e.getMessage());
        }
    }
}