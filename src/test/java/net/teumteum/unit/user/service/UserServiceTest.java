package net.teumteum.unit.user.service;


import static net.teumteum.unit.common.SecurityValue.VALID_ACCESS_TOKEN;
import static net.teumteum.unit.common.SecurityValue.VALID_REFRESH_TOKEN;
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
import net.teumteum.meeting.domain.MeetingFixture;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.WithdrawReasonRepository;
import net.teumteum.user.domain.request.ReviewRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserWithdrawRequest;
import net.teumteum.user.domain.response.UserRegisterResponse;
import net.teumteum.user.domain.response.UserReviewResponse;
import net.teumteum.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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
    @DisplayName("회원 로그아웃 API는")
    class Logout_user_api_unit {

        @Test
        @DisplayName("정상적인 요청시, 회원 로그아웃을 진행하고 200 OK을 반환한다.")
        void If_valid_user_logout_request_return_200_OK() {
            // given
            Long userId = 1L;
            doNothing().when(redisService).deleteData(anyString());

            // when
            userService.logout(userId);

            // then
            verify(redisService, times(1)).deleteData(anyString());
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 API는")
    class Withdraw_user_api_unit {

        @Test
        @DisplayName("유저 id에 해당하는 유저가 존재하지 않는 경우, 400 Bad Request 을 반환한다.")
        void Return_400_bad_request_if_user_is_not_exist() {
            // given
            UserWithdrawRequest request
                = RequestFixture.userWithdrawRequest(List.of("쓰지 않는 앱이에요", "오류가 생겨서 쓸 수 없어요"));

            long userId = 1L;

            given(userRepository.findById(anyLong()))
                .willThrow(new IllegalArgumentException("userId 에 해당하는 user를 찾을 수 없습니다."));

            // When & Then
            assertThatThrownBy(() -> userService.withdraw(request, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId 에 해당하는 user를 찾을 수 없습니다.");
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

            var meeting = MeetingFixture.getCloseMeetingWithIdAndParticipantIds(1L, List.of(1L, 2L, 10L));
            var userId = 10L;

            given(meetingConnector.findById(anyLong()))
                .willReturn(Optional.of(meeting));

            given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(UserFixture.getUserWithId(userId)));

            // when
            userService.registerReview(meeting.getId(), userId, reviewRegisterRequest);

            // then
            verify(meetingConnector, times(1)).findById(anyLong());
            verify(userRepository, times(3)).findById(anyLong());
        }

        @Test
        @DisplayName("meeting id 에 해당하는 meeting 이 존재하지 않는 경우, 400 Bad Request 와 함께 리뷰 등록을 실패한다.")
        void Return_400_bad_request_if_meeting_is_not_exist() {
            // given
            var reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            var meeting = MeetingFixture.getCloseMeetingWithId(1L);
            var currentUserId = 1L;

            given(meetingConnector.findById(anyLong()))
                .willReturn(Optional.empty());
            // when & then
            assertThatThrownBy(() -> userService.registerReview(meeting.getId(), currentUserId, reviewRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("meetingId에 해당하는 모임을 찾을 수 없습니다. \"" + meeting.getId() + "\"");
        }

        @Test
        @DisplayName("meeting id 에 해당하는 meeting 이 아직 종료되지 않았다면, 400 Bad Request 와 함께 리뷰 등록을 실패한다.")
        void Return_400_bad_request_if_meeting_is_not_closed() {
            // given
            var reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            var meeting = MeetingFixture.getOpenMeetingWithId(1L);
            var currentUserId = 1L;

            given(meetingConnector.findById(anyLong()))
                .willReturn(Optional.of(meeting));

            // when & then
            assertThatThrownBy(() -> userService.registerReview(meeting.getId(), currentUserId, reviewRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 모임은 아직 종료되지 않았습니다.");
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
            var existUser = UserFixture.getIdUser();

            var response = List.of(new UserReviewResponse(최고에요, 2L)
                , new UserReviewResponse(별로에요, 3L));

            given(userRepository.findById(anyLong())).willReturn(Optional.of(existUser));
            given(userRepository.countUserReviewsByUser(any(User.class))).willReturn(response);

            // when
            var result = userService.getUserReviews(userId);

            // then
            assertThat(result.reviews()).hasSize(2);
            assertThat(result.reviews().get(0).review()).isEqualTo(최고에요);
            assertThat(result.reviews().get(0).count()).isEqualTo(2L);
            assertThat(result.reviews().get(1).review()).isEqualTo(별로에요);

            verify(userRepository, times(1)).countUserReviewsByUser(any(User.class));
        }
    }
}
