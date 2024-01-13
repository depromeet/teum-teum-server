package net.teumteum.core.security.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public String getData(String key) {
        ValueOperations<String, String> valueOperations = getStringStringValueOperations();
        return valueOperations.get(key);
    }

    public void setData(String key, String value) {
        ValueOperations<String, String> valueOperations = getStringStringValueOperations();
        valueOperations.set(key, value);
    }

    public void setDataWithExpiration(String key, String value, Long duration) {
        ValueOperations<String, String> valueOperations = getStringStringValueOperations();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        this.stringRedisTemplate.delete(key);
    }

    private ValueOperations<String, String> getStringStringValueOperations() {
        return this.stringRedisTemplate.opsForValue();
    }
}
