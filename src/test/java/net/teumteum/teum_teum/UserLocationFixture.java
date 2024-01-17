package net.teumteum.teum_teum;

import lombok.Builder;
import net.teumteum.teum_teum.domain.UserAroundLocationsResponse.UserAroundLocationResponse;
import net.teumteum.teum_teum.domain.UserLocationRequest;

public class UserLocationFixture {

    public static UserLocationRequest getDefaultUserLocationRequest() {
        return newUserLocationRequest(UserLocationBuilder.builder().build());
    }

    public static UserAroundLocationResponse getDefaultUserAroundLocationResponse() {
        return newUserAroundLocationResponse(UserAroundLocationResponseBuilder.builder().build());
    }

    public static UserLocationRequest newUserLocationRequest(UserLocationBuilder userLocationBuilder) {
        return new UserLocationRequest(
            userLocationBuilder.longitude,
            userLocationBuilder.latitude,
            userLocationBuilder.id,
            userLocationBuilder.name,
            userLocationBuilder.jobDetailClass,
            userLocationBuilder.characterId
        );
    }

    public static UserAroundLocationResponse newUserAroundLocationResponse(
        UserAroundLocationResponseBuilder userAroundLocationResponseBuilder) {
        return new UserAroundLocationResponse(
            userAroundLocationResponseBuilder.id,
            userAroundLocationResponseBuilder.name,
            userAroundLocationResponseBuilder.jobDetailClass,
            userAroundLocationResponseBuilder.characterId
        );
    }

    @Builder
    public static class UserLocationBuilder {

        @Builder.Default
        private Double longitude = 120.5;

        @Builder.Default
        private Double latitude = -22.3;

        @Builder.Default
        private Long id = 1L;

        @Builder.Default
        private String name = "John";

        @Builder.Default
        private String jobDetailClass = "backend";

        @Builder.Default
        private Long characterId = 3L;
    }

    @Builder
    public static class UserAroundLocationResponseBuilder {

        @Builder.Default
        private Long id = 1L;

        @Builder.Default
        private String name = "Mike";

        @Builder.Default
        private String jobDetailClass = "design";

        @Builder.Default
        private Long characterId = 1L;
    }
}
