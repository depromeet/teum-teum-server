package net.teumteum.unit.user.controller;

import static net.teumteum.unit.auth.common.SecurityValue.VALID_ACCESS_TOKEN;
import static net.teumteum.unit.auth.common.SecurityValue.VALID_REFRESH_TOKEN;
import static net.teumteum.user.domain.Review.별로에요;
import static net.teumteum.user.domain.Review.최고에요;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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
            mockMvc.perform(post("/users/withdraw")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
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
    }


    @Nested
    @DisplayName("회원 리뷰 조회 API는")
    class Get_user_reviews_api_unit {

        @Test
        @DisplayName("로그인한 회원 id 에 해당하는 회원 리뷰와 200 OK을 반환한다.")
        void Get_user_reviews_with_200_ok() throws Exception {
            // given
            var userId = 1L;

            given(securityService.getCurrentUserId()).willReturn(userId);

            given(userService.getUserReviews(anyLong()))
                .willReturn(List.of(new UserReviewsResponse(별로에요, 2L),
                    new UserReviewsResponse(최고에요, 3L)));

            // when & then
            mockMvc.perform(get("/users/reviews")
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].count", is(2)))
                .andExpect(jsonPath("$[0].review").value("별로에요"));
        }
    }
}
