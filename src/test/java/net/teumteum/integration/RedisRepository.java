package net.teumteum.integration;

import lombok.RequiredArgsConstructor;
import net.teumteum.core.security.service.RedisService;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;

@TestComponent
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveGeoRedisData(String key, Point point, String member) {
        GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
        geoOperations.add(key, point, member);
    }

    public GeoResults<GeoLocation<Object>> getGeoRedisData(String key, Circle circle) {
        GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
        return geoOperations.radius(key, circle);
    }

    public void saveRedisDataWithExpiration(String key, String value, Long duration) {
        redisService.setDataWithExpiration(key, value, duration);
    }

    void deleteRedisData(String key) {
        redisService.deleteData(key);
    }

    public String getRedisData(String key) {
        return redisService.getData(key);
    }
}
