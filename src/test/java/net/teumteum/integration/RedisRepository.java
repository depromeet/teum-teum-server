package net.teumteum.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.teum_teum.domain.UserLocation;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@TestComponent
@RequiredArgsConstructor
public class RedisRepository {

    private static final String HASH_KEY = "userLocation";
    private final RedisService redisService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    void init() {
        valueOperations = redisTemplate.opsForValue();
    }


    public void setUserLocation(UserLocation userLocation, Long duration) {
        String key = HASH_KEY + userLocation.id();
        String value;
        try {
            value = objectMapper.writeValueAsString(userLocation);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        valueOperations.set(key, value, duration);
    }


    public void saveRedisDataWithExpiration(String key, String value, Long duration) {
        redisService.setDataWithExpiration(key, value, duration);
    }
}

