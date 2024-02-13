package net.teumteum.unit.user.controller;

import static net.teumteum.unit.common.SecurityValue.VALID_ACCESS_TOKEN;
import static net.teumteum.unit.common.SecurityValue.VALID_REFRESH_TOKEN;
import static net.teumteum.user.domain.Review.별로에요;
import static net.teumteum.user.domain.Review.최고에요;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.teumteum.core.security.SecurityConfig;
import net.teumteum.core.security.filter.JwtAuthenticationFilter;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.integration.RequestFixture;
import net.teumteum.user.controller.UserController;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.request.ReviewRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserWithdrawRequest;
import net.teumteum.user.domain.response.UserRegisterResponse;
import net.teumteum.user.domain.response.UserReviewResponse;
import net.teumteum.user.domain.response.UserReviewsResponse;
import net.teumteum.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedisService.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtService.class)}
)
@WithMockUser
@DisplayName("유저 컨트롤러 단위 테스트의")
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @MockBean
    private SecurityService securityService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.getIdUser();
    }

    @Nested
    @DisplayName("유저 카드 등록 API는")
    class Register_user_card_api_unit {

        @Test
        @DisplayName("유효한 사용자의 등록 요청값이 주어지면, 201 Created 상태값을 반환한다.")
        void Register_user_card_with_201_created() throws Exception {
            // given
            UserRegisterRequest request = RequestFixture.userRegisterRequest(user);

            UserRegisterResponse response = new UserRegisterResponse(1L, VALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);

            given(userService.register(any(UserRegisterRequest.class))).willReturn(response);

            // when & then
            mockMvc.perform(post("/users")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accessToken").value(VALID_ACCESS_TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(VALID_REFRESH_TOKEN));
        }

        @Test
        @DisplayName("이미 카드 등록한 사용자의 등록 요청값이 주어지면, 400 Bad Request을 반환한다.")
        void Return_400_bad_request_if_user_already_exist() throws Exception {
            // given
            UserRegisterRequest request = RequestFixture.userRegisterRequest(user);

            given(userService.register(any(UserRegisterRequest.class)))
                .willThrow(new IllegalArgumentException("일치하는 user 가 이미 존재합니다."));

            // when && then
            mockMvc.perform(post("/users")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("일치하는 user 가 이미 존재합니다."));
        }

        @Test
        @DisplayName("유효하지 않은 사용자의 등록 요청값이 주어지면, 400 Bad Request 상태값을 반환한다.")
        void Register_user_card_with_400_bad_request() throws Exception {
            // given
            UserRegisterRequest request = RequestFixture.userRegisterRequestWithNoValid(user);
            // when
            // then
            mockMvc.perform(post("/users")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 API는")
    class Withdraw_user_api_unit {

        @Test
        @DisplayName("회원 탈퇴 사유와 회원 탈퇴 요청이 들어오면, 탈퇴를 진행하고 200 OK을 반환한다.")
        void Withdraw_user_with_200_ok() throws Exception {
            // given
            UserWithdrawRequest request
                = RequestFixture.userWithdrawRequest(List.of("쓰지 않는 앱이에요", "오류가 생겨서 쓸 수 없어요"));

            // when & then
            mockMvc.perform(post("/users/withdraws")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("회원 탈퇴 하고자 하는 회원이 존재하지 않으면, 400 Bad Request을 반환한다.")
        void Return_400_bad_request_if_user_is_not_exist() throws Exception {
            // given
            UserWithdrawRequest request
                = RequestFixture.userWithdrawRequest(List.of("쓰지 않는 앱이에요", "오류가 생겨서 쓸 수 없어요"));

            doThrow(new IllegalArgumentException("일치하는 user가 이미 존재합니다.")).when(userService).withdraw(any(
                UserWithdrawRequest.class), anyLong());

            // when && then
            mockMvc.perform(post("/users/withdraws")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("회원 리뷰 등록 API는")
    class Register_user_review_api_unit {

        @Test
        @DisplayName("회원 id 와 리뷰 정보 요청이 들어오면, 회원 리뷰를 등록하고 200 OK을 반환한다.")
        void Register_user_review_with_200_ok() throws Exception {
            // given
            ReviewRegisterRequest reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            // when & then
            mockMvc.perform(post("/users/reviews")
                    .param("meetingId", String.valueOf(1L))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reviewRegisterRequest))
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("현재 로그인한 회원의 id 가 리뷰 등록 요청에 포함된다면, 회원 리뷰 등록을 실패하고 400 bad request을 반환한다.")
        void Register_reviews_with_400_bad_request() throws Exception {
            // given
            ReviewRegisterRequest reviewRegisterRequest = RequestFixture.reviewRegisterRequest();

            String errorMessage = "나의 리뷰에 대한 리뷰를 작성할 수 없습니다.";

            doThrow(new IllegalArgumentException(errorMessage))
                .when(userService)
                .registerReview(anyLong(), anyLong(), any(ReviewRegisterRequest.class));

            // when & then
            mockMvc.perform(post("/users/reviews")
                    .param("meetingId", String.valueOf(1L))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reviewRegisterRequest))
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(errorMessage, result.getResolvedException().getMessage()));
        }
    }


    @Nested
    @DisplayName("회원 리뷰 조회 API는")
    class Get_user_reviews_api_unit {

        @Test
        @DisplayName("user id 에 해당하는 회원 리뷰와 200 OK을 반환한다.")
        void Get_user_reviews_with_200_ok() throws Exception {
            // given
            var userId = 1L;

            given(userService.getUserReviews(anyLong()))
                .willReturn(UserReviewsResponse.of(List.of(new UserReviewResponse(별로에요, 2L),
                    new UserReviewResponse(최고에요, 3L))));

            // when & then
            mockMvc.perform(get("/users/{userId}/reviews", 1)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews", notNullValue()))
                .andExpect(jsonPath("$.reviews", hasSize(2)))
                .andExpect(jsonPath("$.reviews[0].count", is(2)))
                .andExpect(jsonPath("$.reviews[0].review").value("별로에요"));
        }
    }

    @Nested
    @DisplayName("회원 로그아웃 API는")
    class Logout_user_api_unit {

        @Test
        @DisplayName("로그인한 회원의 로그아웃을 진행하고, 200 OK 을 반환합니다.")
        void Logout_user_with_200_ok() throws Exception {
            // given
            doNothing().when(userService).logout(anyLong());

            // when && then
            mockMvc.perform(post("/users/logouts")
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
        }
    }
}
