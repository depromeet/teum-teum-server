package net.teumteum.teum_teum.domain.response;

import java.util.List;
import net.teumteum.teum_teum.domain.UserData;

public record UserAroundLocationsResponse(
    List<UserAroundLocationResponse> userLocations
) {

    public static UserAroundLocationsResponse of(List<UserData> userData) {
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
            UserData userData
        ) {
            return new UserAroundLocationResponse(
                userData.id(),
                userData.name(),
                userData.jobDetailClass(),
                userData.characterId()
            );
        }
    }
}
