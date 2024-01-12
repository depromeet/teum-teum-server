package net.teumteum.integration;

import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("인증 통합테스트의")
public class AuthIntegrationTest extends IntegrationTest {

    private static final String VALID_ACCESS_TOKEN = "VALID_ACCESS_TOKEN";
    private static final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";
    private static final String VALID_REFRESH_TOKEN = "VALID_REFRESH_TOKEN";
    private static final String INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";

    @Nested
    @DisplayName("토큰 재발급 API는")
    class ReIssue_jwt_api {

        @Test
        @DisplayName("유효하지 않은 access token 과 유효한 refresh token 이 주어지면, 새로운 토큰을 발급한다.")
        void Return_new_jwt_if_access_and_refresh_is_exist() {
            // given
            User user = repository.saveAndGetUser();
            // when
            var result = api.reissueJwt(INVALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);
            // then
            Assertions.assertThat(result.expectBody(TokenResponse.class)
                .returnResult()
                .getResponseBody());
        }
    }
}
