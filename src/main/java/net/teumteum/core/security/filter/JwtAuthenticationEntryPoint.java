package net.teumteum.core.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.core.error.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authenticationException
    ) throws IOException {
        this.sendUnAuthenticatedError(response, authenticationException);
    }

    private void sendUnAuthenticatedError(HttpServletResponse response,
        Exception exception) throws IOException {
        OutputStream os = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        log.error("Responding with unauthenticated error. Message - {}", exception.getMessage());
        objectMapper.writeValue(os, ErrorResponse.of("인증 과정에서 오류가 발생했습니다."));
        os.flush();
    }
}
