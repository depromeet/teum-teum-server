package net.teumteum.unit.core.security;

import static net.teumteum.unit.common.SecurityValue.INVALID_ACCESS_TOKEN;
import static net.teumteum.unit.common.SecurityValue.VALID_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import net.teumteum.auth.service.AuthService;
import net.teumteum.core.property.JwtProperty;
import net.teumteum.core.security.UserAuthentication;
import net.teumteum.core.security.filter.JwtAuthenticationFilter;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 단위 테스트의")
public class JwtAuthenticationFilterTest {

    @Mock
    JwtService jwtService;

    @Mock
    AuthService authService;

    @Mock
    JwtProperty jwtProperty;

    @Mock
    JwtProperty.Access access;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Nested
    @DisplayName("API 요청시 JWT 파싱 및 회원 조회 로직은")
    class Api_request_with_valid_jwt_unit {

        @BeforeEach
        @AfterEach
        void clearSecurityContextHolder() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("유효한 JWT 인 경우, JWT 을 파싱하고 성공적으로 UserAuthentication 을 SecurityContext 에 저장한다.")
        void Parsing_jwt_and_save_user_in_security_context() throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            given(jwtProperty.getAccess()).willReturn(access);
            given(jwtProperty.getAccess().getHeader()).willReturn("Authorization");
            given(jwtProperty.getBearer()).willReturn("Bearer");

            request.addHeader(jwtProperty.getAccess().getHeader(),
                jwtProperty.getBearer() + " " + VALID_ACCESS_TOKEN);

            User user = UserFixture.getIdUser();

            given(jwtService.validateToken(anyString())).willReturn(true);
            given(authService.findUserByAccessToken(anyString())).willReturn(user);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isInstanceOf(UserAuthentication.class);
        }

        @Test
        @DisplayName("유효하지 않은 JWT 와 함께 요청이 들어오면, 요청 처리를 중단하고 에러 메세지를 반환한다.")
        void Return_error_when_jwt_is_invalid() throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            given(jwtProperty.getAccess()).willReturn(access);
            given(jwtProperty.getAccess().getHeader()).willReturn("Authorization");
            given(jwtProperty.getBearer()).willReturn("Bearer");

            request.addHeader(jwtProperty.getAccess().getHeader(),
                jwtProperty.getBearer() + " " + INVALID_ACCESS_TOKEN);

            given(jwtService.validateToken(anyString())).willReturn(false);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            assertThat(request.getAttribute("exception")).isEqualTo("요청에 대한 JWT 가 유효하지 않습니다.");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNull();
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("JWT 가 존재하지 않는 경우, 요청 처리를 중단하고 에러 메세지를 반환한다.")
        void Return_error_when_jwt_is_not_exist() throws ServletException, IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            given(jwtProperty.getAccess()).willReturn(access);
            given(jwtProperty.getAccess().getHeader()).willReturn("Authorization");
            given(jwtProperty.getBearer()).willReturn("Bearer");

            request.addHeader(jwtProperty.getAccess().getHeader(),
                jwtProperty.getBearer() + " ");

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            assertThat(request.getAttribute("exception")).isEqualTo("요청에 대한 JWT 정보가 존재하지 않습니다.");
            verify(jwtService, times(0)).validateToken(anyString());
        }
    }
}
