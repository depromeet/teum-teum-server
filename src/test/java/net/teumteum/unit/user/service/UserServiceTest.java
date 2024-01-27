package net.teumteum.unit.user.service;

import static net.teumteum.unit.auth.common.SecurityValue.VALID_ACCESS_TOKEN;
import static net.teumteum.unit.auth.common.SecurityValue.VALID_REFRESH_TOKEN;
import static net.teumteum.user.domain.Review.별로에요;
import static net.teumteum.user.domain.Review.최고에요;
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
import net.teumteum.meeting.domain.MeetingConnector;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.WithdrawReasonRepository;
import net.teumteum.user.domain.request.ReviewRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserWithdrawRequest;
import net.teumteum.user.domain.response.UserRegisterResponse;
import net.teumteum.user.domain.response.UserReviewsResponse;
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

    @Mock
    MeetingConnector meetingConnector;

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

    @Nested
    @DisplayName("회원 리뷰 등록 API는")
    class Register_user_review_api_unit {

        @Test
        @DisplayName("회원 id 와 리뷰 정보 요청이 들어오면, 회원 리뷰를 등록하고 200 OK을 반환한다.")
        void Register_user_review_with_200_ok() {
            // given
            ReviewRegisterRequest reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            Long meetingId = 1L;

            Long userId = 10L;

            Long currentUserId = 20L;

            given(meetingConnector.existById(anyLong()))
                .willReturn(true);

            given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(UserFixture.getUserWithId(userId++)));

            // when
            userService.registerReview(meetingId, currentUserId, reviewRegisterRequest);

            // then
            verify(meetingConnector, times(1)).existById(anyLong());
            verify(userRepository, times(3)).findById(anyLong());
        }

        @Test
        @DisplayName("회원 id 가 리뷰 정보 요청에 포함되면, 400 Bad Request 와 함께 리뷰 등록을 실패한다.")
        void Return_400_bad_request_if_current_user_id_in_request() {
            // given
            ReviewRegisterRequest reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            Long meetingId = 1L;

            Long currentUserId = reviewRegisterRequest.reviews().get(0).id();

            given(meetingConnector.existById(anyLong()))
                .willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.registerReview(meetingId, currentUserId, reviewRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("나의 리뷰에 대한 리뷰를 작성할 수 없습니다.");
        }

        @Test
        @DisplayName("meeting id 에 해당하는 meeting 이 존재하지 않는 경우, 400 Bad Request 와 함께 리뷰 등록을 실패한다.")
        void Return_400_bad_request_if_meeting_is_not_exist() {
            // given
            ReviewRegisterRequest reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            Long meetingId = 1L;
            Long currentUserId = 1L;

            given(meetingConnector.existById(anyLong()))
                .willReturn(false);
            // when & then
            assertThatThrownBy(() -> userService.registerReview(meetingId, currentUserId, reviewRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("meetingId에 해당하는 meeting을 찾을 수 없습니다. \"" + meetingId + "\"");
        }
    }

    @Nested
    @DisplayName("회원 리뷰 조회 API는")
    class Get_user_reviews_api_unit {

        @Test
        @DisplayName("로그인한 회원의 리뷰 리스트로 200 OK 응답한다.")
        void Return_user_reviews_with_200_ok() {
            // given
            var userId = 1L;

            var response = List.of(new UserReviewsResponse(최고에요, 2L)
                , new UserReviewsResponse(별로에요, 3L));

            given(userRepository.countUserReviewsByUserId(anyLong())).willReturn(response);

            // when
            var result = userService.getUserReviews(userId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).review()).isEqualTo(최고에요);
            assertThat(result.get(0).count()).isEqualTo(2L);
            assertThat(result.get(1).review()).isEqualTo(별로에요);
            assertThat(result.get(1).count()).isEqualTo(3L);

            verify(userRepository, times(1)).countUserReviewsByUserId(anyLong());
        }
    }
}
