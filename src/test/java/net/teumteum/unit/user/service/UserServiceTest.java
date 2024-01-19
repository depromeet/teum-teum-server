package net.teumteum.unit.user.service;

import static net.teumteum.unit.auth.common.SecurityValue.VALID_ACCESS_TOKEN;
import static net.teumteum.unit.auth.common.SecurityValue.VALID_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.integration.RequestFixture;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.WithdrawReasonRepository;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserWithdrawRequest;
import net.teumteum.user.domain.response.UserRegisterResponse;
import net.teumteum.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 서비스 단위 테스트의")
public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    WithdrawReasonRepository withdrawReasonRepository;

    @Mock
    RedisService redisService;

    @Mock
    JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.getIdUser();
    }

    @Nested
    @DisplayName("유저 카드 등록 API는")
    class Register_user_card_api_unit {

        @Test
        @DisplayName("유효한 유저의 요청 값이 들어오는 경우, 정상적으로 유저 카드를 등록한다.")
        void If_valid_user_request_register_user_card() {
            // given
            UserRegisterRequest request = RequestFixture.userRegisterRequest(user);
            TokenResponse tokenResponse = new TokenResponse(VALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);

            UserRegisterResponse response = UserRegisterResponse.of(1L, tokenResponse);

            given(userRepository.save(any(User.class))).willReturn(user);

            given(jwtService.createServiceToken(any(User.class))).willReturn(tokenResponse);

            // when
            UserRegisterResponse result = userService.register(request);

            // then
            assertThat(response.id()).isEqualTo(1);
            assertThat(response.accessToken()).isNotNull();
            assertThat(response.refreshToken()).isNotNull();
        }

        @Test
        @DisplayName("사용자가 이미 존재하면, 카드 등록을 실패한다.")
        void If_user_already_exist_register_user_card_fail() {
            // given
            UserRegisterRequest request = RequestFixture.userRegisterRequestWithFail(user);

            given(userRepository.findByAuthenticatedAndOAuthId(any(), any()))
                .willThrow(new IllegalArgumentException("일치하는 user 가 이미 존재합니다."));

            assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일치하는 user 가 이미 존재합니다.");
        }
    }

    @Nested
    @DisplayName("유저 탈퇴 API는")
    class Withdraw_user_api_unit {

        @Test
        @DisplayName("유효한 유저 회원 탈퇴 요청이 들어오는 경우, 회원을 탈퇴하고 탈퇴 사유 데이터를 저장한다.")
        void If_valid_user_withdraw_request_withdraw_user() {
            // given
            UserWithdrawRequest request
                = RequestFixture.userWithdrawRequest(List.of("쓰지 않는 앱이에요", "오류가 생겨서 쓸 수 없어요"));

            given(userRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(user));

            doNothing().when(userRepository).delete(any());

            doNothing().when(redisService).deleteData(anyString());
            // when
            userService.withdraw(request, user.getId());
            // then
            verify(userRepository, times(1)).findById(anyLong());
            verify(redisService, times(1)).deleteData(anyString());
            verify(withdrawReasonRepository, times(1)).saveAll(any());
        }
    }
}
