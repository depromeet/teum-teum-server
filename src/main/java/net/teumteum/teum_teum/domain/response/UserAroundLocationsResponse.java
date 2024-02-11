package net.teumteum.teum_teum.domain.response;

import java.util.List;
import java.util.Set;
import net.teumteum.teum_teum.domain.UserLocation;

public record UserAroundLocationsResponse(
    List<UserAroundLocationResponse> aroundUserLocations
) {

    public static UserAroundLocationsResponse of(Set<UserLocation> userData) {
        return new UserAroundLocationsResponse(
            userData.stream()
                .map(UserAroundLocationResponse::of)
                .toList()
        );
    }

    public record UserAroundLocationResponse(
        Long id,
        String name,
        String jobDetailClass,
        Long characterId
    ) {

        public static UserAroundLocationResponse of(
            UserLocation userLocation
        ) {
            return new UserAroundLocationResponse(
                userLocation.id(),
                userLocation.name(),
                userLocation.jobDetailClass(),
                userLocation.characterId()
            );
        }
    }
}
