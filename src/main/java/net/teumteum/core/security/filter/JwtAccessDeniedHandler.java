package net.teumteum.core.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        this.sendUnAuthorizedError(response, accessDeniedException);
    }

    private void sendUnAuthorizedError(HttpServletResponse response,
                                       Exception exception) throws IOException {
        log.error("Responding with unauthorized error. Message - {}", exception.getMessage());
        response.sendError(SC_FORBIDDEN, exception.getMessage());
    }
}
