package net.teumteum.core.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

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
        log.error("Responding with unauthenticated error. Message - {}", exception.getMessage());
        response.sendError(SC_UNAUTHORIZED, exception.getMessage());
    }
}
