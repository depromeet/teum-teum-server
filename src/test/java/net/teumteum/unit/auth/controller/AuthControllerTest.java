package net.teumteum.unit.auth.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.HttpServletRequest;
import net.teumteum.auth.controller.AuthController;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.auth.service.AuthService;
import net.teumteum.core.security.SecurityConfig;
import net.teumteum.core.security.filter.JwtAuthenticationFilter;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
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

@WebMvcTest(value = AuthController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedisService.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtService.class)}
)
@WithMockUser
@DisplayName("인증 컨트롤러 단위 테스트의")
public class AuthControllerTest {

    private static final String VALID_ACCESS_TOKEN = "VALID_ACCESS_TOKEN";
    private static final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";
    private static final String VALID_REFRESH_TOKEN = "VALID_REFRESH_TOKEN";
    private static final String INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("토큰 재발급 API는")
    class Reissue_jwt_api_unit {

        @Test
        @DisplayName("유효하지 않은 access token 과 유효한 refresh token 이 주어지면, 새로운 토큰을 발급한다.")
        void Return_new_jwt_if_access_and_refresh_is_exist() throws Exception {
            // given
            TokenResponse tokenResponse = new TokenResponse(INVALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);

            given(authService.reissue(any(HttpServletRequest.class))).willReturn(tokenResponse);
            // when & then
            mockMvc.perform(post("/auth/reissues")
                    .with(csrf())
                    .header(AUTHORIZATION, INVALID_ACCESS_TOKEN)
                    .header("Authorization-refresh", VALID_REFRESH_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
        }

        @Test
        @DisplayName("유효하지 않은 access token 과 유효하지 않은 refresh token 이 주어지면, 500 Server Error를 응답한다.")
        void Return_500_bad_request_if_refresh_token_is_not_valid() throws Exception {
            // given
            given(authService.reissue(any(HttpServletRequest.class))).willThrow(
                new IllegalArgumentException("refresh token 이 유효하지 않습니다."));

            // when & then
            mockMvc.perform(post("/auth/reissues")
                    .with(csrf())
                    .header(AUTHORIZATION, INVALID_ACCESS_TOKEN)
                    .header("Authorization-refresh", INVALID_REFRESH_TOKEN))
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value("refresh token 이 유효하지 않습니다."));
        }
    }
}
