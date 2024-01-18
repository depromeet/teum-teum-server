package net.teumteum.teum_teum.service;

import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofMinutes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.teum_teum.domain.UserData;
import net.teumteum.teum_teum.domain.request.UserLocationRequest;
import net.teumteum.teum_teum.domain.response.UserAroundLocationsResponse;
import net.teumteum.teum_teum.domain.response.UserAroundLocationsResponse.UserAroundLocationResponse;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeumTeumService {

    private static final String KEY = "userLocation";
    private static final int SEARCH_LIMIT = 6;
    private static final Duration LOCATION_EXPIRATION = ofMinutes(1);

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public UserAroundLocationsResponse processingUserAroundLocations(UserLocationRequest request) {
        GeoOperations<String, Object> geoValueOperations = redisTemplate.opsForGeo();

        String userDataJson = null;
        try {
            userDataJson = objectMapper.writeValueAsString(
                request.toUserData()) + ":" + currentTimeMillis();
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException Occurred!");
        }

        geoValueOperations.add(KEY, new Point(request.longitude(), request.latitude()), userDataJson);

        return getUserAroundLocations(geoValueOperations, request.longitude(), request.latitude());
    }

    private UserAroundLocationsResponse getUserAroundLocations(GeoOperations<String, Object> geoValueOperations,
        Double longitude, Double latitude) {

        GeoResults<GeoLocation<Object>> geoResults
            = geoValueOperations.radius(KEY,
            new Circle(new Point(longitude, latitude), new Distance(100, Metrics.METERS)));

        return getUserAroundLocationsResponse(geoResults);
    }

    private UserAroundLocationsResponse getUserAroundLocationsResponse(GeoResults<GeoLocation<Object>> geoResults) {

        List<UserAroundLocationResponse> userAroundLocationResponses = new ArrayList<>();

        long currentTime = currentTimeMillis();
        int count = 0;

        for (GeoResult<GeoLocation<Object>> geoResult : Objects.requireNonNull(geoResults)) {
            String userSavedTime = String.valueOf(geoResult.getContent().getName()).split(":")[1];
            long timestamp = Long.parseLong(userSavedTime);

            if (currentTime - timestamp < LOCATION_EXPIRATION.toMillis()) {
                String userDataJson = String.valueOf(geoResult.getContent().getName()).split(":")[0];
                UserData userData = null;
                try {
                    userData = objectMapper.readValue(userDataJson, UserData.class);
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException Occurred!");
                }

                UserAroundLocationResponse userAroundLocationResponse
                    = UserAroundLocationResponse.of(Objects.requireNonNull(userData));

                userAroundLocationResponses.add(userAroundLocationResponse);
                count++;

                if (count >= SEARCH_LIMIT) {
                    break;
                }
            }
        }
        return new UserAroundLocationsResponse(userAroundLocationResponses);
    }
}
