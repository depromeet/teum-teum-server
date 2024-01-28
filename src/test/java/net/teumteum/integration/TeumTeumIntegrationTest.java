package net.teumteum.integration;

import java.util.List;
import net.teumteum.teum_teum.UserLocationFixture;
import net.teumteum.teum_teum.domain.UserLocation;
import net.teumteum.teum_teum.domain.response.UserAroundLocationsResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

@DisplayName("틈틈 서비스 통합테스트의")
public class TeumTeumIntegrationTest extends IntegrationTest {

    @Nested
    @DisplayName("회원 위치 저장 로직은")
    class Save_user_location_logic {

        private static final String VALID_TOKEN = "VALID_TOKEN";
        private static final String INVALID_TOKEN = "IN_VALID_TOKEN";

        @BeforeEach
        void init() throws JsonProcessingException {
            UserLocation userLocation01 = new UserLocation(100L, 20.2, 20.2, "Selly", "frontend", 1L);
            UserLocation userLocation02 = new UserLocation(101L, 20.2, 20.2, "John", "Design", 2L);

            redisRepository.setUserLocation(userLocation01, 60L);
            redisRepository.setUserLocation(userLocation02, 60L);
        }

        @Test
        @DisplayName("회원의 올바른 요청이 오는 경우, 정상적으로 저장하고, 100 m 이내의 유저 정보를 가져온다.")
        void if_user_request_valid_save_successfully() {
            // given
            var userLocationRequest = UserLocationFixture.getDefaultUserLocationRequest();

            var expected = List.of(UserLocationFixture.getDefaultUserAroundLocationResponse());
            // when
            var result = api.getTeumteumAround(VALID_TOKEN, userLocationRequest);

            // then
            Assertions.assertThat(
                    result.expectBody(UserAroundLocationsResponse.class).returnResult()
                        .getResponseBody())
                .usingRecursiveComparison()
                .isNotNull();
        }
    }
}
