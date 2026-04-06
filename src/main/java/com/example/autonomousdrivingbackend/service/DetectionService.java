package com.example.autonomousdrivingbackend.service;

import com.example.autonomousdrivingbackend.dto.DetectionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "detection:";
    private static final long TTL_MINUTES = 30;

    public void saveResult(DetectionResult result) {
        try {
            String key = KEY_PREFIX + result.getImage();
            String value = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(key, value, TTL_MINUTES, TimeUnit.MINUTES);
            log.info("Redis 저장 완료: {}", key);
        } catch (Exception e) {
            log.error("Redis 저장 오류: {}", e.getMessage());
        }
    }

    public DetectionResult getResult(String imageName) {
        try {
            String key = KEY_PREFIX + imageName;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.info("캐시 미스: {}", key);
                return null;
            }
            log.info("캐시 히트: {}", key);
            return objectMapper.readValue(value, DetectionResult.class);
        } catch (Exception e) {
            log.error("Redis 조회 오류: {}", e.getMessage());
            return null;
        }
    }
}
