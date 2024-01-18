package net.teumteum.integration;

import java.util.List;
import net.teumteum.teum_teum.UserLocationFixture;
import net.teumteum.teum_teum.domain.UserData;
import net.teumteum.teum_teum.domain.response.UserAroundLocationsResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("틈틈 서비스 통합테스트의")
public class TeumTeumIntegrationTest extends IntegrationTest {

    @Nested
    @DisplayName("회원 위치 저장 로직은")
    class Save_user_location_logic {

        private static final String VALID_TOKEN = "VALID_TOKEN";
        private static final String INVALID_TOKEN = "IN_VALID_TOKEN";
        private static final String KEY = "userLocation";
        private static final long currentTimeMillis = 2000L;

        @BeforeEach
        void init() throws JsonProcessingException {
            Point point01 = new Point(120.3, -22.4);
            Point point02 = new Point(120.4, -22.2);

            UserData userData01 = new UserData(100L, "Selly", "frontend", 1L);
            UserData userData02 = new UserData(101L, "Tom", "UX design", 5L);

            redisRepository.saveGeoRedisData(KEY, point01,
                new ObjectMapper().writeValueAsString(userData01 + ":" + currentTimeMillis));

            redisRepository.saveGeoRedisData(KEY, point02,
                new ObjectMapper().writeValueAsString(userData02 + ":" + currentTimeMillis));
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
                .isNull();
        }
    }
}
