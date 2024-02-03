package net.teumteum.teum_teum.service;

import static java.lang.Math.atan2;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static java.util.Comparator.comparingDouble;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.teum_teum.domain.UserLocation;
import net.teumteum.teum_teum.domain.request.UserLocationRequest;
import net.teumteum.teum_teum.domain.response.UserAroundLocationsResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeumTeumService {

    private static final int SEARCH_LIMIT = 6;

    private final RedisService redisService;

    public UserAroundLocationsResponse saveAndGetUserAroundLocations(UserLocationRequest request) {
        redisService.setUserLocation(request.toUserLocation(), 60L);
        return getUserAroundLocations(request);
    }

    private UserAroundLocationsResponse getUserAroundLocations(UserLocationRequest request) {
        Set<UserLocation> allUserLocations = redisService.getAllUserLocations();

        List<UserLocation> aroundUserLocations = allUserLocations.stream()
            .filter(userLocation -> !userLocation.id().equals(request.id()))
            .filter(userLocation -> calculateDistance(request.latitude(), request.longitude(),
                userLocation.latitude(), userLocation.longitude()) <= 100)
            .sorted(comparingDouble(userLocation
                -> calculateDistance(request.latitude(), request.longitude(),
                userLocation.latitude(), userLocation.longitude()))
            ).limit(SEARCH_LIMIT)
            .toList();

        return UserAroundLocationsResponse.of(aroundUserLocations);
    }

    private double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        final int earthRadius = 6371;
        double latDistance = toRadians(latitude2 - latitude1);
        double lonDistance = toRadians(longitude2 - longitude1);
        double a = sin(latDistance / 2) * sin(latDistance / 2)
            + Math.cos(toRadians(latitude1)) * Math.cos(toRadians(latitude2))
            * sin(lonDistance / 2) * sin(lonDistance / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadius * c * 1000;
    }
}
