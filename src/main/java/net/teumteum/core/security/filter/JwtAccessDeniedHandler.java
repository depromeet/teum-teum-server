package net.teumteum.core.security.filter;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.core.error.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        this.sendUnAuthorizedError(response, accessDeniedException);
    }

    private void sendUnAuthorizedError(HttpServletResponse response,
        Exception exception) throws IOException {
        response.setStatus(SC_FORBIDDEN);
        OutputStream os = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        log.error("Responding with unauthorized error. Message - {}", exception.getMessage());
        objectMapper.writeValue(os, ErrorResponse.of("인가 과정에서 오류가 발생했습니다."));
        os.flush();
    }
}
