package net.teumteum.unit.core.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.core.security.filter.JwtAuthenticationEntryPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationEntryPoint 단위 테스트의")
public class JwtAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationException authenticationException;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Nested
    @DisplayName("JwtAuthenticationFilter 에서 인증 예외가 발생시")
    class When_authentication_error_occurs_from_filter {

        @Test
        @DisplayName("알맞은 예외 메시지와 관련 응답을 반환한다.")
        void Return_error_response_with_message() throws IOException {
            // given
            var errorMessage = "Authentication Exception Occurred";
            var outputStream = new ByteArrayOutputStream();

            given(request.getAttribute("exception")).willReturn(errorMessage);
            given(response.getOutputStream()).willReturn(new DelegatingServletOutputStream(outputStream));

            // when
            jwtAuthenticationEntryPoint.commence(request, response, authenticationException);

            // then
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(objectMapper, times(1)).writeValue(any(OutputStream.class), any(ErrorResponse.class));
        }
    }
}
