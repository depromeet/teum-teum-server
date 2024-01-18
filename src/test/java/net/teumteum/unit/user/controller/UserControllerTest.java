package net.teumteum.unit.user.controller;

import static net.teumteum.unit.auth.common.SecurityValue.VALID_ACCESS_TOKEN;
import static net.teumteum.unit.auth.common.SecurityValue.VALID_REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.teumteum.core.security.SecurityConfig;
import net.teumteum.core.security.filter.JwtAuthenticationFilter;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.integration.RequestFixture;
import net.teumteum.user.controller.UserController;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.response.UserRegisterResponse;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

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
                    .content(new ObjectMapper().writeValueAsString(request))
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
                    .content(new ObjectMapper().writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }
}
