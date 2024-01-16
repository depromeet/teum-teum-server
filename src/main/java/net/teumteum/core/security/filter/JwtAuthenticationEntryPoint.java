package net.teumteum.core.security.filter;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

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

    private static final String ATTRIBUTE_NAME = "exception";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authenticationException) throws IOException {
        this.sendUnAuthenticatedError(request, response, authenticationException);
    }

    private void sendUnAuthenticatedError(HttpServletRequest request, HttpServletResponse response, Exception exception)
        throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        OutputStream os = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        log.error("Responding with unauthenticated error. Message - {}", exception.getMessage());
        objectMapper.writeValue(os, ErrorResponse.of((String) request.getAttribute(ATTRIBUTE_NAME)));
        os.flush();
    }
}
