package net.teumteum.unit.auth.service;

import static net.teumteum.core.security.Authenticated.네이버;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.auth.service.AuthService;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 서비스 단위 테스트의")
public class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    JwtService jwtService;

    @Mock
    RedisService redisService;

    @Mock
    UserConnector userConnector;

    @Nested
    @DisplayName("토큰 재발급 API는")
    class Reissue_jwt_api_unit {

        @Test
        @DisplayName("유효하지 않은 access token 과  유효한 refresh token 이 주어지면, 새로운 토큰을 발급한다.")
        void Return_new_jwt_if_access_and_refresh_is_exist() {
            // given
            Optional<User> user = Optional.of(new User(1L, "oauthId", 네이버));

            HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

            given(jwtService.extractAccessToken(any(HttpServletRequest.class))).willReturn("access token");

            given(jwtService.extractRefreshToken(any(HttpServletRequest.class))).willReturn("refresh token");

            given(jwtService.getUserIdFromToken(anyString())).willReturn("1");

            given(jwtService.createAccessToken(anyString())).willReturn("new access token");

            given(jwtService.createRefreshToken()).willReturn("new refresh token");

            given(redisService.getData(anyString())).willReturn("refresh token");

            given(userConnector.findUserById(anyLong())).willReturn(user);

            given(jwtService.validateToken(anyString())).willReturn(true);

            // when
            TokenResponse response = authService.reissue(httpServletRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new access token");
            assertThat(response.getRefreshToken()).isEqualTo("new refresh token");
            verify(userConnector, times(1)).findUserById(anyLong());
            verify(jwtService, times(1)).validateToken(any());
        }

        @Test
        @DisplayName("유효하지 않은 access token 과 유효하지 않은 refresh token 이 주어지면, 500 server 에러로 응답한다. ")
        void Return_500_bad_request_if_refresh_token_is_not_valid() {
            Optional<User> user = Optional.of(new User(1L, "oauthId", 네이버));

            HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

            given(jwtService.extractAccessToken(any(HttpServletRequest.class))).willReturn("access token");

            given(jwtService.extractRefreshToken(any(HttpServletRequest.class))).willReturn("refresh token");

            given(jwtService.validateToken(anyString())).willReturn(true);

            given(jwtService.getUserIdFromToken(anyString())).willReturn("1");

            given(userConnector.findUserById(anyLong())).willReturn(user);

            given(redisService.getData(anyString())).willThrow(
                new IllegalArgumentException("refresh token 이 일치하지 않습니다."));

            // when
            assertThatThrownBy(() -> authService.reissue(httpServletRequest)).isInstanceOf(
                IllegalArgumentException.class).hasMessage("refresh token 이 일치하지 않습니다.");

        }
    }
}
