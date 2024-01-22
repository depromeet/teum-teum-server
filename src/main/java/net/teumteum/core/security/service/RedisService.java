package net.teumteum.core.security.service;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.teumteum.teum_teum.domain.UserLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {


    private static final String HASH_KEY = "userLocation";
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    public String getData(String key) {
        return valueOperations.get(key);
    }

    public void setData(String key, String value) {
        valueOperations.set(key, value);
    }

    public void setDataWithExpiration(String key, String value, Long duration) {
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        valueOperations.getOperations().delete(key);
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

    public Set<UserLocation> getAllUserLocations() {
        Set<String> keys = redisTemplate.keys(HASH_KEY + ":*");
        return requireNonNull(keys).stream().map(key -> {
            String value = valueOperations.get(key);
            try {
                return objectMapper.readValue(value, UserLocation.class);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }).collect(Collectors.toSet());
    }
}
