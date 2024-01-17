package net.teumteum.unit.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import net.teumteum.core.security.service.RedisService;
import net.teumteum.integration.RequestFixture;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.request.UserRegisterRequest;
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
    RedisService redisService;

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

            given(userRepository.save(any(User.class))).willReturn(user);

            // when
            UserRegisterResponse response = userService.register(request);

            // then
            assertThat(response.id()).isEqualTo(1);
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
}
